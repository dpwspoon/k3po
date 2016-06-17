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

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.socket.nio.NioDatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramWorkerPool;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.net.InetSocketAddress;
import java.net.URI;

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;

class UdpServerChannelSink extends AbstractServerChannelSink<UdpServerChannel> {
    private final NioDatagramChannelFactory serverChannelFactory;

    UdpServerChannelSink(NioDatagramWorkerPool workerPool) {
        serverChannelFactory = new NioDatagramChannelFactory(workerPool);
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        // Use ConnectionlessBootstrap to create a NioDatagramChannel for an UdpServerChannel
        UdpServerChannel serverChannel = (UdpServerChannel) evt.getChannel();
        ConnectionlessBootstrap bootstrap = new ConnectionlessBootstrap(serverChannelFactory);
        DatagramChannelPipelineFactory pipelineFactory = new DatagramChannelPipelineFactory(serverChannel);
        bootstrap.setPipelineFactory(pipelineFactory);
        ChannelAddress localAddress = (ChannelAddress) evt.getValue();
        NioDatagramChannel datagramChannel = (NioDatagramChannel) bootstrap.bind(toInetSocketAddress(localAddress));
        serverChannel.setDatagramChannel(datagramChannel);

        evt.getFuture().setSuccess();
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        UdpServerChannel serverChannel = (UdpServerChannel) evt.getChannel();
        serverChannel.getDatagramChannel().unbind();

        fireChannelUnbound(serverChannel);
        evt.getFuture().setSuccess();
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        UdpServerChannel serverChannel = (UdpServerChannel) evt.getChannel();

        // Close underlying NioDatagramChannel
        serverChannel.getDatagramChannel().close();

        // setClosed() (but *not* evt.getFuture().setSuccess()) triggers the ChannelFuture's success
        serverChannel.setClosed();
        fireChannelDisconnected(serverChannel);
        fireChannelUnbound(serverChannel);
        fireChannelClosed(serverChannel);
    }

    private static InetSocketAddress toInetSocketAddress(ChannelAddress channelAddress) {
        if (channelAddress == null) {
            return null;
        }
        URI location = channelAddress.getLocation();
        String hostname = location.getHost();
        int port = location.getPort();
        return new InetSocketAddress(hostname, port);
    }

}
