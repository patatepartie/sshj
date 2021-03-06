/*
 * Copyright 2010 Shikhar Bhushan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.schmizz.sshj.transport;

import net.schmizz.sshj.common.DisconnectReason;
import net.schmizz.sshj.util.BasicFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Disconnection {

    private final BasicFixture fixture = new BasicFixture();

    private boolean notified;

    @Before
    public void setUp()
            throws IOException {
        fixture.init();

        notified = false;

        fixture.getClient().getTransport().setDisconnectListener(new DisconnectListener() {
            @Override
            public void notifyDisconnect(DisconnectReason reason) {
                notified = true;
            }
        });

    }

    @After
    public void tearDown()
            throws IOException, InterruptedException {
        fixture.done();
    }

    private boolean joinToClientTransport(int seconds) {
        try {
            fixture.getClient().getTransport().join(seconds, TimeUnit.SECONDS);
            return true;
        } catch (TransportException ignored) {
            return false;
        }
    }

    @Test
    public void listenerNotifiedOnClientDisconnect()
            throws IOException {
        fixture.stopClient();
        assertTrue(notified);
    }

    @Test
    public void listenerNotifiedOnServerDisconnect()
            throws InterruptedException, IOException {
        fixture.stopServer();
        joinToClientTransport(2);
        assertTrue(notified);
    }

    @Test
    public void joinNotifiedOnClientDisconnect()
            throws IOException {
        fixture.stopClient();
        assertTrue(joinToClientTransport(2));
    }

    @Test
    public void joinNotifiedOnServerDisconnect()
            throws TransportException, InterruptedException {
        fixture.stopServer();
        assertFalse(joinToClientTransport(2));
    }

}