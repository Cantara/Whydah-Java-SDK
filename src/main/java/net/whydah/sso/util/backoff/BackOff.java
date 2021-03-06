/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.whydah.sso.util.backoff;

/**
 * Provide a {@link BackOffExecution} that indicates the rate at which
 * an operation should be retried.
 * <p>
 * <p>Users of this interface are expected to use it like this:
 * <p>
 * <pre class="code">
 * BackOffExecution exec = backOff.start();
 * <p>
 * // In the operation recovery/retry loop:
 * long waitInterval = exec.nextBackOffMillis();
 * if (waitInterval == BackOffExecution.STOP) {
 * // do not retry operation
 * }
 * else {
 * // sleep, e.g. Thread.sleep(waitInterval)
 * // retry operation
 * }
 * }</pre>
 * <p>
 * Once the underlying operation has completed successfully,
 * the execution instance can be simply discarded.
 *
 * @author Stephane Nicoll
 * @see BackOffExecution
 * @since 4.1
 */
public interface BackOff {

    /**
     * Start a new back off execution.
     *
     * @return a fresh {@link BackOffExecution} ready to be used
     */
    BackOffExecution start();

}
