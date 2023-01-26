package com.njkol.perfume.operator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;

import com.njkol.perfume.operator.controller.PerfumeOrderController;
import com.njkol.perfume.operator.controller.model.v1alpha1.PerfumerOrder;

/**
 * Main Class for Operator, you can run this sample using this command:
 * <p>
 * mvn exec:java -Dexec.mainClass=com.njkol.perfume.operator.PerfumeOrderOperatorMain
 */
public class PerfumeOrderOperatorMain {

    public static final Logger logger = LoggerFactory.getLogger(PerfumeOrderOperatorMain.class.getSimpleName());

    public static void main(String[] args) {
    	
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            String namespace = client.getNamespace();
            if (namespace == null) {
                logger.info("No namespace found via config, assuming default.");
                namespace = "default";
            }

            logger.info("Using namespace : {}", namespace);

            SharedInformerFactory informerFactory = client.informers();

            MixedOperation<PerfumerOrder, KubernetesResourceList<PerfumerOrder>, Resource<PerfumerOrder>> podSetClient = client.resources(PerfumerOrder.class);
            SharedIndexInformer<Pod> podSharedIndexInformer = informerFactory.sharedIndexInformerFor(Pod.class, 10 * 60 * 1000L);
            SharedIndexInformer<PerfumerOrder> podSetSharedIndexInformer = informerFactory.sharedIndexInformerFor(PerfumerOrder.class, 10 * 60 * 1000L);
            
            PerfumeOrderController podSetController = new PerfumeOrderController(client, podSetClient, podSharedIndexInformer, podSetSharedIndexInformer, namespace);
            Future<Void> startedInformersFuture = informerFactory.startAllRegisteredInformers();
            startedInformersFuture.get();
            podSetController.run();
            
        } catch (KubernetesClientException | ExecutionException exception) {
            logger.error("Kubernetes Client Exception : ", exception);
        } catch (InterruptedException interruptedException) {
            logger.error("Interrupted: ", interruptedException);
            Thread.currentThread().interrupt();
        }
    }
}
