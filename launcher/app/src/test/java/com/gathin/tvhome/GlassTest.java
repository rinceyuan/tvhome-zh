package com.gathin.tvhome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlassTest {

    @Test
    public void blurPixelsPreservesColorChannelsWithoutOverflowOnWarmEdges() {
        int w = 40;
        int h = 40;
        int[] px = new int[w * h];
        // Left/top half: warm bright orange-gold (high R=255, medium G=90, low B=0).
        // Right/bottom half: cool dark blue-grey (low R=10, medium G=40, high B=220).
        // Under the old R/B-swapped sliding window, this transition caused b/n < 0 (all-white
        // bit-OR corruption) and b/n > 255 (bright green bit-8 overflow).
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                boolean warm = (x < w / 2) ^ (y >= h / 2);
                int r = warm ? 255 : 10;
                int g = warm ? 90 : 40;
                int b = warm ? 0 : 220;
                px[y * w + x] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }

        Glass.blurPixels(px, w, h);

        for (int i = 0; i < px.length; i++) {
            int c = px[i];
            int a = (c >>> 24) & 0xFF;
            int r = (c >>> 16) & 0xFF;
            int g = (c >>> 8) & 0xFF;
            int b = c & 0xFF;

            assertEquals("Alpha must stay 255 at pixel " + i, 0xFF, a);
            assertTrue("Red must stay within [10, 255] at pixel " + i + ", got " + r, r >= 10 && r <= 255);
            // Green in input is strictly between 40 and 90; box blur is a convex combination,
            // so green can never exceed 90 or drop below 40 unless another channel overflows into it.
            assertTrue("Green must stay within [40, 90] at pixel " + i + ", got " + g, g >= 40 && g <= 90);
            assertTrue("Blue must stay within [0, 220] at pixel " + i + ", got " + b, b >= 0 && b <= 220);
        }

        // Verify top-left corner remains predominantly warm (R > B) and top-right remains cool (B > R).
        int topLeft = px[0];
        assertTrue(((topLeft >>> 16) & 0xFF) > (topLeft & 0xFF));
        int topRight = px[w - 1];
        assertTrue((topRight & 0xFF) > ((topRight >>> 16) & 0xFF));
    }
}
