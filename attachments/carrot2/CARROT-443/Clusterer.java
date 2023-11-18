/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.clustering.carrot2;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.nutch.clustering.HitsCluster;
import org.apache.nutch.clustering.OnlineClusterer;
import org.apache.nutch.searcher.HitDetails;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;

import com.carrotsearch.lingo3g.Lingo3GClusteringAlgorithm;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;



/**
 * This plugin provides an implementation of {@link OnlineClusterer} 
 * extension using clustering components of the Carrot2 project
 * (<a href="http://www.carrot2.org">http://www.carrot2.org</a>).
 */
public class Clusterer implements OnlineClusterer, Configurable {
  /** Default language property name. */
  private final static String CONF_PROP_DEFAULT_LANGUAGE =
    "extension.clustering.carrot2.defaultLanguage";

  public static final Log logger = LogFactory.getLog(Clusterer.class);  

  /** The Controller instance used for clustering */
  private Controller controller;

  /** Nutch configuration. */
  private Configuration conf;

  /** 
   * Default language for hits. English by default, but may be changed
   * via a property in Nutch configuration. 
   */
  private LanguageCode defaultLanguage = LanguageCode.ENGLISH;

  /**
   * An empty public constructor for making new instances
   * of the clusterer.
   */
  public Clusterer() {
    // Don't forget to call {@link #setConf(Configuration)}.
  }

  /**
   * See {@link OnlineClusterer} for documentation.
   */
  public HitsCluster [] clusterHits(HitDetails [] hitDetails, String [] descriptions) {
    if (this.controller == null) {
      logger.error("initialize() not called.");
      return new HitsCluster[0];
    }

    if (hitDetails == null) {
      throw new ProcessingException("Hit details array must not be null.");
    }

    if (descriptions  == null) {
      throw new ProcessingException("Descriptions array must not be null.");
    }

    if (hitDetails.length != descriptions.length) {
      throw new ProcessingException("Descriptions and hit details must be of the same length.");
    }
    
    // Prepare documents for Carrot2
    final List<Document> documents = Lists.newArrayListWithCapacity(hitDetails.length);
    for (int i = 0; i < descriptions.length; i++) {
			final HitDetails hit = hitDetails[i];
			final Document document = new Document(hit.getValue("title"),
					descriptions[i], hit.getValue("url"));
			
			// Try to set language
			final String lang = hit.getValue("lang");
			if (StringUtils.isNotBlank(lang)) {
				final LanguageCode carrot2Language = LanguageCode.forISOCode(lang);
			  document.setLanguage(carrot2Language != null ? carrot2Language : defaultLanguage);
			}
			
			documents.add(document);
    }
    final Map<String, Object> attributes = Maps.newHashMap();
    attributes.put(AttributeNames.DOCUMENTS, documents);
    

    try {
    	// Perform clustering
			final List<Cluster> carrotClusters = controller.process(attributes,
					Lingo3GClusteringAlgorithm.class).getClusters();
      final HitsCluster [] clusters = HitsClusterAdapter.adapt(carrotClusters, hitDetails);
      return clusters;
    } catch (ProcessingException e) {
      throw new RuntimeException("Problems with the clustering.", e);
    }
  }

  /**
   * Implementation of {@link Configurable}
   */
  public void setConf(Configuration conf) {
    this.conf = conf;

    // Configure default language and other component settings.
    if (conf.get(CONF_PROP_DEFAULT_LANGUAGE) != null) {
      // Change the default language.
      final String defaultLanguage = conf.get(CONF_PROP_DEFAULT_LANGUAGE);
      final LanguageCode languageCode = LanguageCode.forISOCode(defaultLanguage);
      if (languageCode != null) {
      	this.defaultLanguage = languageCode;
      }
    } 

    if (logger.isInfoEnabled()) {
      logger.info("Default language: " + defaultLanguage);
    }

    initialize();
  }

  /**
   * Implementation of {@link Configurable}
   */
  public Configuration getConf() {
    return conf;
  }
  
  /**
   * Initialize clustering processes and Carrot2 components.
   */
  private synchronized void initialize() {
    // Initialize language list, temporarily switching off logging
    // of warnings. This is a bit of a hack, but we don't want to
    // redistribute the entire Carrot2 distro and this prevents
    // nasty ClassNotFound warnings.
    final Logger c2Logger = Logger.getLogger("org.carrot2");
    final Level original = c2Logger.getLevel();
    c2Logger.setLevel(Level.ERROR);
    c2Logger.setLevel(original);

    // Initialize the controller.    
    controller = ControllerFactory.createPooling();
  }
}
