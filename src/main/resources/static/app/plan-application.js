var planApp = angular.module('PlanApplication', ['ngResource', 'ngRoute']);

planApp.factory('Plans', ['$resource', function($resource) {
    return $resource('/api/plans', {}, {
        query: {method: 'GET', isArray: true},
        create: {method: 'POST'}
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
        $location.path('/detail/' + planId);
    }
    
    $scope.deletePlan = function(planId) {
        Plans.delete({id: planId});
        $scope.plans = Plans.query();
    }
    
    $scope.createNewPlan = function() {
        $location.path('/new');
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


planApp.controller('CreatePlanController', ['$scope', '$location', '$routeParams', 'Plans', function($scope, $location, $routeParams, Plans) {
    $scope.plan = {};
    
    $scope.cancel = function() {
        $location.path('/');
    }
    
    $scope.createPlan = function() {
        Plans.create($scope.plan);
        $location.path('/');
    }
}]);

planApp.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'app/plan/list.html',
        controller: 'PlanListController'
    }).when('/detail/:id', {
        templateUrl: 'app/plan/detail.html',
        controller: 'PlanDetailController'
    }).when('/new', {
        templateUrl: 'app/plan/new-plan.html',
        controller: 'CreatePlanController'
    });
}]);

planApp.filter('cut', function() {
    return function (value, wordwise, max, tail) {
        if (!value) return '';

        max = parseInt(max, 10);
        if (!max) return value;
        if (value.length <= max) return value;

        value = value.substr(0, max);
        if (wordwise) {
            var lastspace = value.lastIndexOf(' ');
            if (lastspace != -1) {
                value = value.substr(0, lastspace);
            }
        }

        return value + (tail || ' …');
    };
});