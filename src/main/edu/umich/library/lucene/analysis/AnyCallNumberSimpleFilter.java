package edu.umich.library.lucene.analysis;

import edu.umich.library.library_identifier.normalizers.AnyCallNumberSimple;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * A Solr filter that take an LC/Dewey Call Number (/ shelf key) and
 * turns it into something that can be sorted correctly _and_
 * can be used for left-anchored search if turned into edge-ngrams.
 * <p>
 */

public final class AnyCallNumberSimpleFilter extends TokenFilter {
  /**
   * Logger used to log info/warnings.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(MethodHandles.lookup().lookupClass());

  /**
   * The filter term that is a result of the conversion.
   */
  private final CharTermAttribute myTermAttribute =
      addAttribute(CharTermAttribute.class);

  /**
   * Should we pass through an invalid (doesn't look like) callnumber,
   * or return nothing (default)
   */

  private Boolean passThroughInvalid;

  /**
   * @param aStream A {@link TokenStream} that parses streams with
   *                ISO-639-1 and ISO-639-2 codes
   */
  public AnyCallNumberSimpleFilter(TokenStream aStream, Boolean passThroughInvalid) {
    super(aStream);
    this.passThroughInvalid = passThroughInvalid;
  }

  public AnyCallNumberSimpleFilter(TokenStream aStream) {
    this(aStream, false);
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
        AnyCallNumberSimple cn = new AnyCallNumberSimple(t);
        // Passing through anything? Just get whatever collation key its got
        if (passThroughInvalid) {
          myTermAttribute.append(cn.any_collation_key());
        } else if (cn.isValid) {
          myTermAttribute.append((cn.collation_key()));
        } else {
          return false; // no passthrough, not valid ==> :-(
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