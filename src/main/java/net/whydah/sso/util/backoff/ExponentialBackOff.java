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
 * Implementation of {@link BackOff} that increases the back off period for each
 * retry attempt. When the interval has reached the {@link #setMaxInterval(long)
 * max interval}, it is no longer increased. Stops retrying once the
 * {@link #setMaxElapsedTime(long) max elapsed time} has been reached.
 * <p>
 * <p>Example: The default interval is {@value #DEFAULT_INITIAL_INTERVAL} ms,
 * the default multiplier is {@value #DEFAULT_MULTIPLIER}, and the default max
 * interval is {@value #DEFAULT_MAX_INTERVAL}. For 10 attempts the sequence will be
 * as follows:
 * <p>
 * <pre>
 * request#     back off
 *
 *  1              2000
 *  2              3000
 *  3              4500
 *  4              6750
 *  5             10125
 *  6             15187
 *  7             22780
 *  8             30000
 *  9             30000
 * 10             30000
 * </pre>
 * <p>
 * <p>Note that the default max elapsed time is {@link Long#MAX_VALUE}. Use
 * {@link #setMaxElapsedTime(long)} to limit the maximum length of time
 * that an instance should accumulate before returning
 * {@link BackOffExecution#STOP}.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class ExponentialBackOff implements BackOff {

    /**
     * The default initial interval.
     */
    public static final long DEFAULT_INITIAL_INTERVAL = 2000L;

    /**
     * The default multiplier (increases the interval by 50%).
     */
    public static final double DEFAULT_MULTIPLIER = 1.5;

    /**
     * The default maximum back off time.
     */
    public static final long DEFAULT_MAX_INTERVAL = 30000L;

    /**
     * The default maximum elapsed time.
     */
    public static final long DEFAULT_MAX_ELAPSED_TIME = Long.MAX_VALUE;


    private long initialInterval = DEFAULT_INITIAL_INTERVAL;

    private double multiplier = DEFAULT_MULTIPLIER;

    private long maxInterval = DEFAULT_MAX_INTERVAL;

    private long maxElapsedTime = DEFAULT_MAX_ELAPSED_TIME;


    /**
     * Create an instance with the default settings.
     *
     * @see #DEFAULT_INITIAL_INTERVAL
     * @see #DEFAULT_MULTIPLIER
     * @see #DEFAULT_MAX_INTERVAL
     * @see #DEFAULT_MAX_ELAPSED_TIME
     */
    public ExponentialBackOff() {
    }

    /**
     * Create an instance with the supplied settings.
     *
     * @param initialInterval the initial interval in milliseconds
     * @param multiplier      the multiplier (should be greater than or equal to 1)
     */
    public ExponentialBackOff(long initialInterval, double multiplier) {
        checkMultiplier(multiplier);
        this.initialInterval = initialInterval;
        this.multiplier = multiplier;
    }

    /**
     * Return the initial interval in milliseconds.
     */
    public long getInitialInterval() {
        return initialInterval;
    }

    /**
     * The initial interval in milliseconds.
     */
    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval;
    }

    /**
     * Return the value to multiply the current interval by for each retry attempt.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * The value to multiply the current interval by for each retry attempt.
     */
    public void setMultiplier(double multiplier) {
        checkMultiplier(multiplier);
        this.multiplier = multiplier;
    }

    /**
     * Return the maximum back off time.
     */
    public long getMaxInterval() {
        return maxInterval;
    }

    /**
     * The maximum back off time.
     */
    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval;
    }

    /**
     * Return the maximum elapsed time in milliseconds after which a call to
     * {@link BackOffExecution#nextBackOff()} returns {@link BackOffExecution#STOP}.
     */
    public long getMaxElapsedTime() {
        return maxElapsedTime;
    }

    /**
     * The maximum elapsed time in milliseconds after which a call to
     * {@link BackOffExecution#nextBackOff()} returns {@link BackOffExecution#STOP}.
     */
    public void setMaxElapsedTime(long maxElapsedTime) {
        this.maxElapsedTime = maxElapsedTime;
    }

    @Override
    public BackOffExecution start() {
        return new ExponentialBackOffExecution();
    }

    private void checkMultiplier(double multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("Invalid multiplier '" + multiplier + "'. Should be equal" +
                    "or higher than 1. A multiplier of 1 is equivalent to a fixed interval");
        }
    }


    protected class ExponentialBackOffExecution implements BackOffExecution {

        protected long currentInterval = -1;

        protected long currentElapsedTime = 0;

        @Override
        public long nextBackOff() {
            if (currentElapsedTime >= maxElapsedTime) {
                return STOP;
            }

            long nextInterval = computeNextInterval();
            currentElapsedTime += nextInterval;
            return nextInterval;
        }

        protected long computeNextInterval() {
            long maxInterval = getMaxInterval();
            if (this.currentInterval >= maxInterval) {
                return maxInterval;
            } else if (this.currentInterval < 0) {
                long initialInterval = getInitialInterval();
                this.currentInterval = (initialInterval < maxInterval
                        ? initialInterval : maxInterval);
            } else {
                this.currentInterval = multiplyInterval(maxInterval);
            }
            return currentInterval;
        }

        private long multiplyInterval(long maxInterval) {
            long i = this.currentInterval;
            i *= getMultiplier();
            return (i > maxInterval ? maxInterval : i);
        }


        @Override
        public String toString() {
            String i = (this.currentInterval < 0 ? "n/a" : this.currentInterval + "ms");
            final StringBuilder sb = new StringBuilder("ExponentialBackOff{");
            sb.append("currentInterval=").append(i);
            sb.append(", multiplier=").append(getMultiplier());
            sb.append('}');
            return sb.toString();
        }
    }

}
