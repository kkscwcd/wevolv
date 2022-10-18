package com.wevolv.unionservice.model.enums;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum UnionProvider {
    ELPA("Elpa"),
    WNBPA("Wnbpa"),
    GIBA("Giba");

    public final static Collection<String> unionProvidersNames = Stream
            .of(UnionProvider.values()).map(s -> s.name)
            .collect(Collectors.toList());

    public final String name;

    UnionProvider(String name){
        this.name = name;
    }
}
