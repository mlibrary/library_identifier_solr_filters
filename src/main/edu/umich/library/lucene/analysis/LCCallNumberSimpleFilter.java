package edu.umich.library.lucene.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umich.library.library_identifier.normalizers.LCCallNumberSimple;

import java.io.IOException;


public final class LCCallNumberSimpleFilter extends TokenFilter {
  /**
   * Logger used to log info/warnings.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(LCCallNumberSimpleFilter.class);

  /**
   * The filter term that is a result of the conversion.
   */
  private final CharTermAttribute myTermAttribute =
      addAttribute(CharTermAttribute.class);

  /**
   * Should we pass through an invalid (doesn't look like) callnumber,
   * or return nothing (default)
   */

  private Boolean passThroughInvalid = false;

  /**
   * @param aStream A {@link TokenStream} that parses streams with
   *                ISO-639-1 and ISO-639-2 codes
   */
  public LCCallNumberSimpleFilter(TokenStream aStream, Boolean passThroughInvalid) {
    super(aStream);
    this.passThroughInvalid = passThroughInvalid;
  }

  /**
   * Increments and processes tokens in the ISO-639 code stream.
   *
   * @return True if a value is still available for processing in the token
   * stream; otherwise, false
   */
  @Override
  public boolean incrementToken() throws IOException {
    if (!input.incrementToken()) {
      return false;
    }

    String t = myTermAttribute.toString();
    if (t != null && t.length() != 0) {
      try {
        myTermAttribute.setEmpty();
        LCCallNumberSimple lc = new LCCallNumberSimple(t);
        if (lc.isValid) {
          myTermAttribute.append(lc.collation_key());
        } else if (passThroughInvalid) {
          myTermAttribute.append(lc.invalid_collation_key());
        } else {
          return false;
        }
      } catch (IllegalArgumentException details) {
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info(details.getMessage(), details);
        }
      }
    }

    return true;
  }
}
