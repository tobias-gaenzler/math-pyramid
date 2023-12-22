provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "math_pyramid" {
  name     = "petclinic_terraform"
  location = var.location
}

#resource "azurerm_application_insights" "math_pyramid" {
#  name                = "petclinic-test-appinsights"
#  location            = azurerm_resource_group.math_pyramid.location
#  resource_group_name = azurerm_resource_group.math_pyramid.name
#  application_type    = "web"
#}

resource "azurerm_spring_cloud_service" "math_pyramid" {
  name                = "math-pyramid"
  resource_group_name = var.resource_group_name
  location            = var.location
  sku_name            = "B0" # Basic

  #  trace {
  #    connection_string = azurerm_application_insights.math_pyramid.connection_string
  #    sample_rate       = 10.0
  #  }

  #  config_server_git_setting {
  #    uri          = "https://github.com/azure-samples/spring-petclinic-microservices-config"
  #    label        = "config"
  #    search_paths = ["dir1", "dir2"]
  #  }
}

resource "azurerm_spring_cloud_app" "math_pyramid" {
  name                = "math-pyramid-app"
  resource_group_name = var.resource_group_name
  service_name        = azurerm_spring_cloud_service.math_pyramid.name
  is_public           = true
  https_only          = true

  identity {
    type = "SystemAssigned"
  }
}

resource "azurerm_spring_cloud_java_deployment" "math_pyramid" {
  name                = "deploy-math-pyramid"
  spring_cloud_app_id = azurerm_spring_cloud_app.math_pyramid.id
  instance_count      = 1
  runtime_version     = "Java_17"

  quota {
    cpu = "1"
    memory = "1Gi"
  }

  environment_variables = {
    "Env" : "Staging"
  }
}

resource "azurerm_spring_cloud_active_deployment" "math_pyramid" {
  spring_cloud_app_id = azurerm_spring_cloud_app.math_pyramid.id
  deployment_name     = azurerm_spring_cloud_java_deployment.math_pyramid.name
}