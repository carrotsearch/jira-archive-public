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

import java.util.List;

import org.apache.nutch.clustering.HitsCluster;
import org.apache.nutch.searcher.HitDetails;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;

/**
 * An adapter of Carrot2's {@link Cluster} interface to
 * {@link HitsCluster} interface. 
 */
public class HitsClusterAdapter implements HitsCluster {
  private Cluster cluster;
  private HitDetails [] hits;

  /**
   * Lazily initialized subclusters array.
   */
  private HitsCluster [] subclusters;
  
  /**
   * Lazily initialized documents array.
   */
  private HitDetails [] documents;
  
  /**
   * Creates a new adapter.
   */
  public HitsClusterAdapter(Cluster cluster, HitDetails [] hits) {
    this.cluster = cluster;
    this.hits = hits;
  }

  /*
   * @see org.apache.nutch.clustering.HitsCluster#getSubclusters()
   */
  public HitsCluster[] getSubclusters() {
    if (this.subclusters == null) {
      final List<Cluster> carrotSubclusters = cluster.getSubclusters();
      if (carrotSubclusters == null || carrotSubclusters.size() == 0) {
        subclusters = null;
      } else {
        subclusters = adapt(carrotSubclusters, hits);
      }
    }

    return subclusters;
  }
  
  static HitsCluster [] adapt(List<Cluster> carrotClusters, HitDetails [] hitDetails)
  {
    final HitsCluster [] clusters = new HitsCluster[carrotClusters.size()];
    int j = 0;
    for (Cluster cluster : carrotClusters) {
      clusters[j++] = new HitsClusterAdapter(cluster, hitDetails);
    }
    return clusters;
  }

  /*
   * @see org.apache.nutch.clustering.HitsCluster#getHits()
   */
  public HitDetails[] getHits() {
    if (documents == null) {
      List<Document> carrotDocuments = this.cluster.getDocuments();
      documents = new HitDetails[ carrotDocuments.size() ];
      
      int j = 0;
      for (Document carrotDocument : carrotDocuments) {
        documents[j++] = this.hits[carrotDocument.getId()];
      }
    }

    return documents;
  }

  /*
   * @see org.apache.nutch.clustering.HitsCluster#getDescriptionLabels()
   */
  public String[] getDescriptionLabels() {
    final List<String> phrases = this.cluster.getPhrases();
		return phrases.toArray( new String [ phrases.size() ]);
  }

  /*
   * @see org.apache.nutch.clustering.HitsCluster#isJunkCluster()
   */
  public boolean isJunkCluster() {
    return cluster.isOtherTopics();
  }
}

