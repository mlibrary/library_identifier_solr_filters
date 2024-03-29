package edu.umich.library.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;

/**
 * @author dueberb
 * A Solr filter that take an LC/Dewey Call Number (/ shelf key) and
 * turns it into something that can be sorted correctly. While the
 * fieldType () is better for general use, if you want to do prefix
 * queries, you need to have an analysis chain so you can add the
 * edge ngram filter, so we've got this.
 * <p>
 *
 * <fieldType name="callnumber_prefix_search"  class="solr.TextField">
 *   <analyzer type="index">
 *     <tokenizer class="solr.KeywordTokenizerFactory"/>
 *     <filter class="edu.umich.library.lucene.analysis.AnyCallNumberSimpleFilterFactory" passThroughOnError="true"/>
 *     <filter class="solr.EdgeNGramFilterFactory" maxGramSize="40" minGramSize="2"/>
 *   </analyzer>
 *   <analyzer type="query">
 *     <tokenizer class="solr.KeywordTokenizerFactory"/>
 *     <filter class="edu.umich.library.lucene.analysis.AnyCallNumberSimpleFilterFactory" passThroughOnError="true"/>
 *   </analyzer>
 * </fieldType>
 */
public class AnyCallNumberSimpleFilterFactory extends TokenFilterFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private Boolean passThroughOnError;
  private Boolean allowTruncated;

  public AnyCallNumberSimpleFilterFactory(Map<String, String> args) {
    super(args);
    passThroughOnError = getBoolean(args, "passThroughOnError", false);
    allowTruncated = getBoolean(args, "allowTruncated", true);
  }

  @Override
  public AnyCallNumberSimpleFilter create(TokenStream input) {
    return new AnyCallNumberSimpleFilter(input, this.passThroughOnError, this.allowTruncated);
  }
}
