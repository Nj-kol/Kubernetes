package com.njkol.perfume.operator.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.cache.Lister;

import com.njkol.perfume.operator.controller.model.v1alpha1.PerfumerOrder;

public class PerfumeOrderController {

	public static final Logger logger = LoggerFactory.getLogger(PerfumeOrderController.class.getSimpleName());
	public static final String APP_LABEL = "app";

	private final BlockingQueue<String> workqueue;

	private final SharedIndexInformer<PerfumerOrder> perfumeOrderInformer;
	private final Lister<PerfumerOrder> perfumeOrderLister;

	private final SharedIndexInformer<Pod> podInformer;
	private final Lister<Pod> podLister;

	private final KubernetesClient kubernetesClient;
	private final MixedOperation<PerfumerOrder, KubernetesResourceList<PerfumerOrder>, Resource<PerfumerOrder>> podSetClient;

	public PerfumeOrderController(KubernetesClient kubernetesClient,
			MixedOperation<PerfumerOrder, KubernetesResourceList<PerfumerOrder>, Resource<PerfumerOrder>> podSetClient,
			SharedIndexInformer<Pod> podInformer, SharedIndexInformer<PerfumerOrder> perfumeOrderInformer,
			String namespace) {
		this.kubernetesClient = kubernetesClient;
		this.podSetClient = podSetClient;
		this.perfumeOrderLister = new Lister<>(perfumeOrderInformer.getIndexer(), namespace);
		this.perfumeOrderInformer = perfumeOrderInformer;
		this.podLister = new Lister<>(podInformer.getIndexer(), namespace);
		this.podInformer = podInformer;
		this.workqueue = new ArrayBlockingQueue<>(1024);
		addEventHandlersToSharedIndexInformers();
	}

	public void run() {

		logger.info("Starting PerfumeOrderController controller");
		while (!Thread.currentThread().isInterrupted()) {
			if (podInformer.hasSynced() && perfumeOrderInformer.hasSynced()) {
				break;
			}
		}

		while (true) {
			try {
				logger.info("trying to fetch item from workqueue...");
				if (workqueue.isEmpty()) {
					logger.info("Work Queue is empty");
				}
				String key = workqueue.take();
				Objects.requireNonNull(key, "key can't be null");
				logger.info("Got {}", key);
				if ((!key.contains("/"))) {
					logger.warn("invalid resource key: {}", key);
				}

				// Get the PerfumerOrder resource's name from key which is in format
				// namespace/name
				String name = key.split("/")[1];
				PerfumerOrder podSet = perfumeOrderLister.get(key.split("/")[1]);
				if (podSet == null) {
					logger.error("PodSet {} in workqueue no longer exists", name);
					return;
				}
				reconcile(podSet);

			} catch (InterruptedException interruptedException) {
				Thread.currentThread().interrupt();
				logger.error("controller interrupted..");
			}
		}
	}

	private void addEventHandlersToSharedIndexInformers() {

		perfumeOrderInformer.addEventHandler(new ResourceEventHandler<PerfumerOrder>() {
			@Override
			public void onAdd(PerfumerOrder order) {
				logger.info("Perfume Order {} ADDED", order.getMetadata().getName());
				logger.info("Perfume Description : {}", order.getSpec());
				// enqueuePodSet(order);
			}

			@Override
			public void onUpdate(PerfumerOrder order, PerfumerOrder newOrder) {
				logger.info("Perfume Order {} MODIFIED", order.getMetadata().getName());
				// enqueuePodSet(newOrder);
			}

			@Override
			public void onDelete(PerfumerOrder order, boolean b) {
				// Do nothing
				logger.info("Perfume Order {} DELETED", order.getMetadata().getName());
			}
		});

		podInformer.addEventHandler(new ResourceEventHandler<Pod>() {
			@Override
			public void onAdd(Pod pod) {
				// handlePodObject(pod);
			}

			@Override
			public void onUpdate(Pod oldPod, Pod newPod) {
				if (oldPod.getMetadata().getResourceVersion().equals(newPod.getMetadata().getResourceVersion())) {
					return;
				}
				// handlePodObject(newPod);
			}

			@Override
			public void onDelete(Pod pod, boolean b) {
				// Do nothing
			}
		});
	}
	
	  private Pod createNewPod(PerfumerOrder perfumeOrder) {
		 
		  String kind = "Perfume";
		  String apiVersion = "njkol.io/v1alpha1";
		  
	        return new PodBuilder()
	                .withNewMetadata()
	                  .withGenerateName(perfumeOrder.getMetadata().getName() + "-pod")
	                  .withNamespace(perfumeOrder.getMetadata().getNamespace())
	                  .withLabels(Collections.singletonMap(APP_LABEL, perfumeOrder.getMetadata().getName()))
	                  .addNewOwnerReference()
	                  .withController(true)
	                  .withKind(kind)
	                  .withApiVersion(apiVersion)
	                  .withName(perfumeOrder.getMetadata().getName())
	                 .withUid(perfumeOrder.getMetadata().getUid()).endOwnerReference()
	                .endMetadata()
	                .withNewSpec()
	                  .addNewContainer()
	                  .withName("busybox")
	                  .withImage("busybox")
	                  .withCommand("sleep", "3600")
	                  .endContainer()
	                .endSpec()
	                .build();
	    }

	/**
	 * Tries to achieve the desired state for podset.
	 *
	 * @param podSet specified podset
	 */
	protected void reconcile(PerfumerOrder podSet) {
		// TODO
	}

}
