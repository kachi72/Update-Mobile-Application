package com.systemtech.update.feeds;

import static org.junit.Assert.assertEquals;

import com.systemtech.update.database.Article;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RssParserTest {

    private final RssParser parser = new RssParser();

    @Test
    public void parse_readsSharedRssFieldsAndIgnoresPublisherExtensions() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<rss version=\"2.0\" xmlns:dc=\"urn:dc\" "
                + "xmlns:content=\"http://purl.org/rss/1.0/modules/content/\">"
                + "<channel><title>Example</title><item>"
                + "<title>Shared parser</title>"
                + "<description><![CDATA[A concise <b>summary</b>.]]></description>"
                + "<link>https://example.com/shared-parser</link>"
                + "<pubDate>Sat, 02 Aug 2026 10:00:00 GMT</pubDate>"
                + "<dc:creator>Publisher</dc:creator>"
                + "<content:encoded><![CDATA[Long-form content]]></content:encoded>"
                + "<media><title>Nested title must be ignored</title></media>"
                + "</item></channel></rss>";

        List<Article> articles = parse(xml, "AI/ML");

        assertEquals(1, articles.size());
        Article article = articles.get(0);
        assertEquals("Shared parser", article.getTitle());
        assertEquals("A concise <b>summary</b>.", article.getDescription());
        assertEquals("https://example.com/shared-parser", article.getLink());
        assertEquals("Sat, 02 Aug 2026 10:00:00 GMT", article.getDate());
        assertEquals("AI/ML", article.getCategory());
    }

    @Test
    public void parse_usesAtomCompatibleFallbacks() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<feed xmlns=\"http://www.w3.org/2005/Atom\"><entry>"
                + "<title>Atom entry</title>"
                + "<link rel=\"alternate\" href=\"https://example.com/atom\" />"
                + "<summary>Fallback summary</summary>"
                + "<updated>2026-08-02T10:00:00Z</updated>"
                + "</entry></feed>";

        List<Article> articles = parse(xml, "Networking");

        assertEquals(1, articles.size());
        Article article = articles.get(0);
        assertEquals("Atom entry", article.getTitle());
        assertEquals("Fallback summary", article.getDescription());
        assertEquals("https://example.com/atom", article.getLink());
        assertEquals("2026-08-02T10:00:00Z", article.getDate());
    }

    @Test
    public void parse_usesUrlGuidAndSkipsEntriesWithoutTitleOrLink() throws Exception {
        String xml = "<rss version=\"2.0\"><channel>"
                + "<item><title>GUID link</title>"
                + "<guid>https://example.com/from-guid</guid>"
                + "<content>Fallback content</content></item>"
                + "<item><title>Missing link</title></item>"
                + "<item><link>https://example.com/missing-title</link></item>"
                + "</channel></rss>";

        List<Article> articles = parse(xml, "Data Science");

        assertEquals(1, articles.size());
        assertEquals("https://example.com/from-guid", articles.get(0).getLink());
        assertEquals("Fallback content", articles.get(0).getDescription());
    }

    private List<Article> parse(String xml, String category) throws Exception {
        try (ByteArrayInputStream input = new ByteArrayInputStream(
                xml.getBytes(StandardCharsets.UTF_8))) {
            return parser.parse(input, category);
        }
    }
}
