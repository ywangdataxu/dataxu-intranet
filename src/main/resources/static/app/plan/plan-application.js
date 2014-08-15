var planApp = angular.module('PlanApplication', ['ngResource']);

planApp.factory('Plans', ['$resource', function($resource) {
    return $resource('/api/plans', {});
}]);

planApp.controller('PlanController', ['$scope', 'Plans', function($scope, Plans) {
    $scope.plans = {};
    
    Plans.query(function(response) {
        $scope.plans = response;
    });
}]);