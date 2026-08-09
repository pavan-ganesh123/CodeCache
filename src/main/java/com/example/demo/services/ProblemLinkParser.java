package com.example.demo.services;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

import com.example.demo.model.Platform;

/**
 * Derives platform + a raw question slug from a problem URL, now that
 * this no longer comes from the (paused) Python scraping service.
 *
 * LeetCode / CodeChef: the path segment right after "problems".
 * Codeforces: contestId + index, from either URL shape Codeforces uses
 *   (/contest/{id}/problem/{index} or /problemset/problem/{id}/{index}).
 * CSES: the raw task number from "task/{number}" — ProblemService maps
 *   that number to a readable name separately, since CSES has no slug
 *   in the URL at all.
 */
public class ProblemLinkParser {

    public record ParsedLink(Platform platform, String questionSlug) {}

    public static ParsedLink parse(String link) {
        URI uri = URI.create(link.trim());
        String host = uri.getHost() != null ? uri.getHost().toLowerCase(Locale.ROOT) : "";
        String[] segments = pathSegments(uri);

        if (host.contains("leetcode.com")) {
            return new ParsedLink(Platform.LEETCODE, segmentAfter(segments, "problems", link));
        }
        if (host.contains("codechef.com")) {
            return new ParsedLink(Platform.CODECHEF, segmentAfter(segments, "problems", link));
        }
        if (host.contains("cses.fi")) {
            return new ParsedLink(Platform.CSES, segmentAfter(segments, "task", link));
        }
        if (host.contains("codeforces.com")) {
            return new ParsedLink(Platform.CODEFORCES, parseCodeforces(segments, link));
        }

        throw new IllegalArgumentException("Unrecognized platform for link: " + link);
    }

    private static String[] pathSegments(URI uri) {
        String path = uri.getPath() != null ? uri.getPath() : "";
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);
    }

    private static String segmentAfter(String[] segments, String marker, String link) {
        for (int i = 0; i < segments.length - 1; i++) {
            if (segments[i].equalsIgnoreCase(marker)) {
                return segments[i + 1];
            }
        }
        throw new IllegalArgumentException(
                "Couldn't find a problem slug in this link: " + link
        );
    }

    private static String parseCodeforces(String[] segments, String link) {
        Integer contestIdx = indexOf(segments, "contest");
        Integer problemIdx = indexOf(segments, "problem");

        // /contest/{contestId}/problem/{index}
        if (contestIdx != null && problemIdx != null
                && contestIdx + 1 < segments.length && problemIdx + 1 < segments.length) {
            return segments[contestIdx + 1] + segments[problemIdx + 1];
        }

        // /problemset/problem/{contestId}/{index}
        if (problemIdx != null && problemIdx + 2 < segments.length) {
            return segments[problemIdx + 1] + segments[problemIdx + 2];
        }

        throw new IllegalArgumentException(
                "Couldn't parse a Codeforces problem from this link: " + link
        );
    }

    private static Integer indexOf(String[] segments, String value) {
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return null;
    }
}