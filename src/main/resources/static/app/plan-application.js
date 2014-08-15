var planApp = angular.module('PlanApplication', ['ngResource', 'ngRoute']);

planApp.factory('Plans', ['$resource', function($resource) {
    return $resource('/api/plans', {}, {
        query: {method: 'GET', isArray: true}
    });
}]);

planApp.factory('Plan', ['$resource', function($resource) {
    return $resource('/api/plans/:id', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {id: '@id'}},
        'delete': {method: 'DELETE', params: {id: '@id'}}
    });
}]);

planApp.controller('PlanListController', ['$scope', '$location', 'Plans', function($scope, $location, Plans) {
    $scope.plans = Plans.query(); 
    
    $scope.editPlan = function(planId) {
        $location.path('/' + planId);
    }
    
    $scope.deletePlan = function(planId) {
        Plans.delete({id: planId});
        $scope.plans = Plans.query();
    }
}]);


planApp.controller('PlanDetailController', ['$scope', '$location', '$routeParams', 'Plan', function($scope, $location, $routeParams, Plan) {
    $scope.plan = Plan.show({id: $routeParams.id});
    
    $scope.cancel = function() {
        $location.path('/');
    }
    
    $scope.updatePlan = function() {
        Plan.update($scope.plan);
        $location.path('/');
    }
}]);


planApp.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'app/plan/list.html',
        controller: 'PlanListController'
    }).when('/:id', {
        templateUrl: 'app/plan/detail.html',
        controller: 'PlanDetailController'
    });
}]);