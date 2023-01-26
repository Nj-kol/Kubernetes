# Perfume Order Operator

## Setup

```shell
BASE_LOC=${HOME}/eclipse-workspaces/demos/perfume-operator
cd ${BASE_LOC}
```

## Build & Run Operator/Controller locally

### How to Build

```shell
mvn clean install
```

### How to Run

```shell
export KUBECONFIG=${HOME}/.k3d/kubeconfig-tescolocal.yaml
mvn exec:java -Dexec.mainClass=com.njkol.perfume.operator.PerfumeOrderOperatorMain
```


* In the controller logs, you'll immediately see the events :

```shell
[-1410008781-pool-1-thread-3] INFO PerfumeOrderController - Perfume Order ysl-la-nuit-de-lhomme ADDED
[-1458080507-pool-1-thread-2] INFO PerfumeOrderController - Perfume Description : PerfumerOrderSpec [brandName=Yves Saint Laurent, perfumeName=La Nuit de l'Homme, concentration=EDP, orders=1]
[-1410008781-pool-1-thread-4] INFO PerfumeOrderController - Perfume Order allure-homme-sport-extreme ADDED
[-1458080507-pool-1-thread-3] INFO PerfumeOrderController - Perfume Description : PerfumerOrderSpec [brandName=Chanel, perfumeName=Allure Homme Sport Extreme, concentration=EDT, orders=2]
[-1410008781-pool-1-thread-4] INFO PerfumeOrderController - Perfume Order ysl-la-nuit-de-lhomme DELETED
[-1410008781-pool-1-thread-4] INFO PerfumeOrderController - Perfume Order allure-homme-sport-extreme DELETED
```


# Running the Operator on k8s

## Build the Docker image

```shell
BASE_PATH=<some_path>/perfume-operator

sudo docker image build -t mysql-user-data-service ${BASE_PATH}

docker build -t nexusdockerhosted.sys.ourtesco.com/perfume-operator .

docker push nexusdockerhosted.sys.ourtesco.com/perfume-operator

docker pull nexusdockerhosted.sys.ourtesco.com/perfume-operator
```

## RBAC Setup

```shell
OPERATOR_BASE_LOC=${HOME}/eclipse-workspaces/demos/perfume-operator/src/main/resources
```

* Create service account

```shell
kubectl apply -f $OPERATOR_BASE_LOC/service-account.yaml

kubectl get serviceaccounts -n demo
```

* Create cluster role

```shell
kubectl apply -f $OPERATOR_BASE_LOC/operator-role.yaml

kubectl get clusterroles | grep cif

kubectl get clusterroles/perfume-operator-role -o yaml

kubectl delete -f $OPERATOR_BASE_LOC/operator-role.yaml -n demo
```

* Create cluster role binding

```shell
kubectl apply -f $OPERATOR_BASE_LOC/operator-role-binding.yaml

kubectl get clusterrolebindings
```

## Deploy to k8s

```shell
kubectl apply -f $OPERATOR_BASE_LOC/operator-deploy.yaml -n demo

kubectl get po -n demo

kubectl logs perfume-operator-6c5547776c-974zb -n demo
```

## Create CRD


```shell 
kubectl apply -f $OPERATOR_BASE_LOC/perfume-order-crd.yaml -n demo

kubectl get crds -n demo
```

## Create Custom Resources

In a separate window, start adding custom resources

```shell
kubectl apply -f ${OPERATOR_BASE_LOC}/ysl-cr.yaml -n demo

kubectl apply -f ${OPERATOR_BASE_LOC}/chanel-cr.yaml
```


```shell
kubectl get perfumes -n demo

kubectl delete -f ${OPERATOR_BASE_LOC}/ysl-cr.yaml

kubectl delete -f ${OPERATOR_BASE_LOC}s/chanel-cr.yaml
```

```shell
kubectl logs perfume-operator-6c5547776c-974zb -n demo -f --tail 100
```

## References
