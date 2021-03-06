/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.main;

import org.elasticsearch.Build;
import org.elasticsearch.Version;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class TransportMainAction extends HandledTransportAction<MainRequest, MainResponse> {

    private final ClusterService clusterService;

    @Inject
    public TransportMainAction(Settings settings, ThreadPool threadPool, TransportService transportService,
                               ActionFilters actionFilters, ClusterService clusterService) {
        super(settings, MainAction.NAME, threadPool, transportService, actionFilters, MainRequest::new);
        this.clusterService = clusterService;
    }

    @Override
    protected void doExecute(MainRequest request, ActionListener<MainResponse> listener) {
        ClusterState clusterState = clusterService.state();
        assert Node.NODE_NAME_SETTING.exists(settings);
        final boolean available = clusterState.getBlocks().hasGlobalBlock(RestStatus.SERVICE_UNAVAILABLE) == false;
        listener.onResponse(
            new MainResponse(Node.NODE_NAME_SETTING.get(settings), Version.CURRENT, clusterState.getClusterName(),
                    clusterState.metaData().clusterUUID(), Build.CURRENT));
    }
}
