package com.atricore.iam.midpoint.connector.convenia;

import java.text.Normalizer;

public class CustomField {

    private String name;

    private long id;

    private String original;

    public CustomField(String original) {
        this.id = original.hashCode();
        this.original = original;

        // Remove accents
        String normalizedString = Normalizer.normalize(original, Normalizer.Form.NFD);
        String withoutAccents = normalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Remove spaces
        String withoutSpaces = withoutAccents.replaceAll("\\s+", "");

        // Remove question mark, to lowercase
        this.name = withoutSpaces.replaceAll("\\?", "").toLowerCase();
   }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getOriginal() {
        return original;
    }
}
