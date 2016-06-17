/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.netty.bootstrap.udp;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannelSource;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpServerChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.util.NavigableMap;

import static org.jboss.netty.channel.Channels.pipeline;

final class UdpChildChannelPipelineFactory implements ChannelPipelineFactory {

    private final NavigableMap<ChannelAddress, UdpServerChannel> udpBindings;

    public UdpChildChannelPipelineFactory(NavigableMap<ChannelAddress, UdpServerChannel> udpBindings) {
        this.udpBindings = udpBindings;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(), new UdpChildChannelSource(null));
    }

}
