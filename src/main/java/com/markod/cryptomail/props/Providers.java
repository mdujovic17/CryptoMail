package com.markod.cryptomail.props;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.HashSet;

@XStreamAlias("providers")
public class Providers {
    @XStreamImplicit(itemFieldName = "providerInformation")
    private HashSet<Provider> providerInformation;

    public HashSet<Provider> getProviders() {
        return providerInformation;
    }
}
