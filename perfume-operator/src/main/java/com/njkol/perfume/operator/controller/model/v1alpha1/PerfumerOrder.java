package com.njkol.perfume.operator.controller.model.v1alpha1;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("njkol.io")
@Plural("perfumes")
@Singular("perfume")
public class PerfumerOrder extends CustomResource<PerfumeOrderSpec, PerfumeOrderStatus> implements Namespaced {
	
	private static final long serialVersionUID = 1L;

	@Override
    public String toString() {
        String name = getMetadata() != null ? getMetadata().getName() : "unknown";
        String version = getMetadata() != null ? getMetadata().getResourceVersion() : "unknown";
        return "name=" + name + " version=" + version + " value=" + spec;
    }
}
