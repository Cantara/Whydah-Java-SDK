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

import java.util.Random;

public class JitteryExponentialBackOff extends ExponentialBackOff {

    public static final int DEFAULT_JITTER_RANGE = 2000;

    int jitterRange = DEFAULT_JITTER_RANGE;

    /**
     * Create an instance with the default settings.
     *
     * @see #DEFAULT_INITIAL_INTERVAL
     * @see #DEFAULT_MULTIPLIER
     * @see #DEFAULT_MAX_INTERVAL
     * @see #DEFAULT_MAX_ELAPSED_TIME
     */
    public JitteryExponentialBackOff() {
    }

    /**
     * Create an instance with the supplied settings.
     *
     * @param initialInterval the initial interval in milliseconds
     * @param multiplier      the multiplier (should be greater than or equal to 1)
     */
    public JitteryExponentialBackOff(long initialInterval, double multiplier) {
        super(initialInterval, multiplier);
    }

    /**
     * @param initialInterval the initial interval in milliseconds
     * @param multiplier      the multiplier (should be greater than or equal to 1)
     * @param jitterRange     next interval will vary with a random number in the range [-jitterRange,+jitterRange)
     */
    public JitteryExponentialBackOff(long initialInterval, double multiplier, int jitterRange) {
        super(initialInterval, multiplier);
        this.jitterRange = jitterRange;
    }

    @Override
    public BackOffExecution start() {
        return new JitteryExponentialBackOffExecution();
    }

    protected class JitteryExponentialBackOffExecution extends ExponentialBackOffExecution {
        private final Random random = new Random(System.currentTimeMillis());

        @Override
        protected long computeNextInterval() {
            long stableNextInterval = super.computeNextInterval();
            int effectiveJitterRange = (int) Math.min(jitterRange, stableNextInterval / 2); // jitter should never exceed more than half of the base interval
            int jitter = random.nextInt(2 * effectiveJitterRange) - effectiveJitterRange;
            return stableNextInterval + jitter;
        }
    }

}
