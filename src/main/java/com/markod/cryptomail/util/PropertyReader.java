package com.markod.cryptomail.util;

import com.markod.cryptomail.props.Provider;
import com.markod.cryptomail.props.Providers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import java.io.File;
import java.util.HashSet;

public class PropertyReader {
    private File JSON;
    private final XStream xStream;

    private PropertyReader() {
        this.xStream = new XStream(new JettisonMappedXmlDriver());
        xStream.processAnnotations(Providers.class);
        xStream.allowTypesByWildcard(new String[] {Providers.class.getPackageName() + "*.**"});
    }

    public PropertyReader(File file) {
        this();
        this.JSON = file;
    }

    public HashSet<Provider> read() {
        Providers p = (Providers) xStream.fromXML(JSON);
        return p.getProviders();
    }
}
