# boogh
This application was generated using JHipster 5.7.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v5.7.0](https://www.jhipster.tech/documentation-archive/v5.7.0).

## Development

To start your application in the dev profile, simply run:

    ./mvnw


For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].



## Building for production

To optimize the boogh application for production, run:

    ./mvnw -Pprod clean package

To ensure everything worked, run:

    java -jar target/*.war


Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./mvnw clean test

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

Then, run a Sonar analysis:

```
./mvnw -Pprod clean test sonar:sonar
```

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw package -Pprod jib:dockerBuild

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: https://www.jhipster.tech
[JHipster 5.7.0 archive]: https://www.jhipster.tech/documentation-archive/v5.7.0

[Using JHipster in development]: https://www.jhipster.tech/documentation-archive/v5.7.0/development/
[Using Docker and Docker-Compose]: https://www.jhipster.tech/documentation-archive/v5.7.0/docker-compose
[Using JHipster in production]: https://www.jhipster.tech/documentation-archive/v5.7.0/production/
[Running tests page]: https://www.jhipster.tech/documentation-archive/v5.7.0/running-tests/
[Code quality page]: https://www.jhipster.tech/documentation-archive/v5.7.0/code-quality/
[Setting up Continuous Integration]: https://www.jhipster.tech/documentation-archive/v5.7.0/setting-up-ci/

# JHipster generated kubernetes configuration

## Preparation

You will need to push your image to a registry. If you have not done so, use the following commands to tag and push the images:

## Deployment

You can deploy all your apps by running the below bash command:

```
./kubectl-apply.sh
```

## Exploring your services


Use these commands to find your application's IP addresses:

```
$ kubectl get svc boogh -n boogh
```

## Scaling your deployments

You can scale your apps using

```
$ kubectl scale deployment <app-name> --replicas <replica-count> -n boogh
```

## zero-downtime deployments

The default way to update a running app in kubernetes, is to deploy a new image tag to your docker registry and then deploy it using

```
$ kubectl set image deployment/<app-name>-app <app-name>=<new-image>  -n boogh
```

Using livenessProbes and readinessProbe allows you to tell kubernetes about the state of your apps, in order to ensure availablity of your services. You will need minimum 2 replicas for every app deployment, you want to have zero-downtime deployed. This is because the rolling upgrade strategy first kills a running replica in order to place a new. Running only one replica, will cause a short downtime during upgrades.

## Monitoring tools


### Prometheus metrics

Generator is also packaged with [Prometheus operator by CoreOS](https://github.com/coreos/prometheus-operator).

**hint**: use must build your apps with `prometheus` profile active!

Application metrics can be explored in Prometheus through,

```
$ kubectl get svc jhipster-prometheus -n boogh
```

Also the visualisation can be explored in Grafana which is pre-configured with a dashboard view. You can find the service details by
```
$ kubectl get svc jhipster-grafana -n boogh
```

* If you have chosen *Ingress*, then you should be able to access Grafana using the given ingress domain.
* If you have chosen *NodePort*, then point your browser to an IP of any of your nodes and use the node port described in the output.
* If you have chosen *LoadBalancer*, then use the IaaS provided LB IP




## Troubleshooting

> my apps doesn't get pulled, because of 'imagePullBackof'

check the registry your kubernetes cluster is accessing. If you are using a private registry, you should add it to your namespace by `kubectl create secret docker-registry` (check the [docs](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/) for more info)

> my apps get killed, before they can boot up

This can occur, if your cluster has low resource (e.g. Minikube). Increase the `initialDelySeconds` value of livenessProbe of your deployments

> my apps are starting very slow, despite I have a cluster with many resources

The default setting are optimized for middle scale clusters. You are free to increase the JAVA_OPTS environment variable, and resource requests and limits to improve the performance. Be careful!

> I have selected prometheus but no targets are visible

This depends on the setup of prometheus operator and the access control policies in your cluster. Version 1.6.0+ is needed for the RBAC setup to work.

> I have selected prometheus, but my targets never get scraped

This means your apps are probably not built using the `prometheus` profile in Maven/Gradle

> my SQL based microservice stuck during liquibase initialization when running multiple replicas

Sometimes the database changelog lock gets corrupted. You will need to connect to the database using `kubectl exec -it` and remove all lines of liquibases `databasechangeloglock` table.

