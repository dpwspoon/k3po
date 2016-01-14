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
package org.kaazing.k3po.pcap.converter.internal.filter;

import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

public class NonEmptyDestPortTcpPacketFilter implements Filter {

    private int port;
    
    public NonEmptyDestPortTcpPacketFilter(int port) {
        super();
        this.port = port;
    }

    @Override
    public boolean passesFilter(Packet pc) throws FilterFailureException {
        if(!pc.isTcp())
            return false;
        if(pc.getTcpPayloadSize() < 1)
            return false;
        if(!(pc.getTcpDestPort() == port))
            return false;
        return true;
    }
}
