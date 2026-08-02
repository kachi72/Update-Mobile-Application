package com.systemtech.update.feeds;

import androidx.annotation.NonNull;

import com.systemtech.update.database.Article;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public final class RssParser {

    @NonNull
    public List<Article> parse(@NonNull InputStream inputStream, @NonNull String category)
            throws IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        configureSecurely(factory);

        try {
            SAXParser parser = factory.newSAXParser();
            parser.getXMLReader().setEntityResolver((publicId, systemId) ->
                    new InputSource(new StringReader("")));

            FeedHandler handler = new FeedHandler(category);
            parser.parse(inputStream, handler);
            return handler.getArticles();
        } catch (ParserConfigurationException | SAXException exception) {
            throw new IOException("Unable to parse RSS response", exception);
        }
    }

    private void configureSecurely(SAXParserFactory factory) {
        setFeatureIfSupported(factory,
                "http://apache.org/xml/features/disallow-doctype-decl", true);
        setFeatureIfSupported(factory,
                "http://xml.org/sax/features/external-general-entities", false);
        setFeatureIfSupported(factory,
                "http://xml.org/sax/features/external-parameter-entities", false);
        setFeatureIfSupported(factory,
                "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    }

    private void setFeatureIfSupported(SAXParserFactory factory, String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        } catch (ParserConfigurationException | SAXException ignored) {
            // Android parser implementations do not all expose the same optional hardening flags.
        }
    }

    private static final class FeedHandler extends DefaultHandler {

        private final String category;
        private final List<Article> articles = new ArrayList<>();

        private EntryBuilder entry;
        private int entryDepth;
        private String capturedField;
        private StringBuilder capturedText;
        private int capturedFieldDepth;

        private FeedHandler(String category) {
            this.category = category;
        }

        @NonNull
        private List<Article> getArticles() {
            return articles;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            String name = normalizedName(localName, qName);

            if (entry == null && ("item".equals(name) || "entry".equals(name))) {
                entry = new EntryBuilder();
                entryDepth = 0;
                return;
            }

            if (entry == null) {
                return;
            }

            entryDepth++;
            if (capturedField != null || entryDepth != 1) {
                return;
            }

            if ("link".equals(name)) {
                String href = attributes.getValue("href");
                String rel = attributes.getValue("rel");
                if (href != null && (rel == null || rel.isEmpty() || "alternate".equals(rel))) {
                    entry.setLinkIfEmpty(href);
                }
            }

            if (isCapturedField(name)) {
                capturedField = name;
                capturedText = new StringBuilder();
                capturedFieldDepth = entryDepth;
            }
        }

        @Override
        public void characters(char[] characters, int start, int length) {
            if (capturedText != null) {
                capturedText.append(characters, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            String name = normalizedName(localName, qName);

            if (entry == null) {
                return;
            }

            if (capturedField != null
                    && capturedFieldDepth == entryDepth
                    && capturedField.equals(name)) {
                entry.accept(capturedField, capturedText.toString());
                capturedField = null;
                capturedText = null;
            }

            if (("item".equals(name) || "entry".equals(name)) && entryDepth == 0) {
                Article article = entry.build(category);
                if (article != null) {
                    articles.add(article);
                }
                entry = null;
                return;
            }

            entryDepth--;
        }

        private boolean isCapturedField(String name) {
            switch (name) {
                case "title":
                case "description":
                case "summary":
                case "encoded":
                case "content":
                case "link":
                case "guid":
                case "pubdate":
                case "published":
                case "updated":
                    return true;
                default:
                    return false;
            }
        }

        private String normalizedName(String localName, String qName) {
            String name = localName == null || localName.isEmpty() ? qName : localName;
            int prefixSeparator = name.indexOf(':');
            if (prefixSeparator >= 0) {
                name = name.substring(prefixSeparator + 1);
            }
            return name.toLowerCase(Locale.US);
        }
    }

    private static final class EntryBuilder {

        private String title = "";
        private String description = "";
        private String fallbackDescription = "";
        private String date = "";
        private String link = "";
        private String guid = "";

        private void accept(String field, String rawValue) {
            String value = rawValue == null ? "" : rawValue.trim();
            if (value.isEmpty()) {
                return;
            }

            switch (field) {
                case "title":
                    title = value;
                    break;
                case "description":
                    description = value;
                    break;
                case "summary":
                case "encoded":
                case "content":
                    if (fallbackDescription.isEmpty()) {
                        fallbackDescription = value;
                    }
                    break;
                case "link":
                    setLinkIfEmpty(value);
                    break;
                case "guid":
                    guid = value;
                    break;
                case "pubdate":
                case "published":
                case "updated":
                    if (date.isEmpty()) {
                        date = value;
                    }
                    break;
                default:
                    break;
            }
        }

        private void setLinkIfEmpty(String value) {
            if (link.isEmpty() && value != null) {
                link = value.trim();
            }
        }

        private Article build(String category) {
            if (link.isEmpty() && (guid.startsWith("https://") || guid.startsWith("http://"))) {
                link = guid;
            }

            if (title.isEmpty() || link.isEmpty()) {
                return null;
            }

            String resolvedDescription = description.isEmpty()
                    ? fallbackDescription
                    : description;
            return new Article(title, date, resolvedDescription, link, category);
        }
    }
}
