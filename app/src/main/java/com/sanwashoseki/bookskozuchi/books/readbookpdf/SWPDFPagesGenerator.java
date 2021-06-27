package com.sanwashoseki.bookskozuchi.books.readbookpdf;

public class SWPDFPagesGenerator {

    static int[] generatePages(int totalPage, boolean isVertical) {
        int[] pageIndices = new int[totalPage];
        for (int pageIndex = 0; pageIndex < totalPage; pageIndex++) {
            pageIndices[pageIndex] = isVertical ? totalPage - 1 - pageIndex : pageIndex;
        }
        return pageIndices;
    }

}
