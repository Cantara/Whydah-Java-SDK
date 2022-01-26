package net.whydah.sso.session;

import net.whydah.sso.whydah.ThreatSignal;

public interface ThreatSignalBuilder {

    ThreatSignalBuilder withClientIpAddress(String clientIpAddress);

    ThreatSignalBuilder withSource(String source);

    ThreatSignalBuilder withText(String text);

    ThreatSignalBuilder withAdditionalProperties(Object[] additionalProperties);

    ThreatSignalBuilder withSeverity(ThreatSignal.SeverityLevel severity);

    ThreatSignalBuilder withImmediateThreat(boolean immediateThreat);

    ThreatSignal build();
}
