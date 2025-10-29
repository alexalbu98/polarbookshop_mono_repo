# Catalog Service
custom_build(
    # Name of the container image
    ref = 'catalog-service',

    skips_local_docker = True,

    # Command to build the container image
    command = './mvnw spring-boot:build-image',

    # Files to watch that trigger a new build
    deps = ['pom.xml', 'catalog-service', 'deployment/K8s']
)

# Deploy
k8s_yaml(['deployment/K8s/services/catalog-service.yml', 'deployment/K8s/services/postgres.yml'])

# Manage
k8s_resource('catalog-service', port_forwards=['9001'])