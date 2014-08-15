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

planApp.factory('Users', ['$resource', function($resource) {
    return $resource('/api/users', {}, {
        query: {method: 'GET', isArray: true},
        create: {method: 'POST'}
    });
}]);

planApp.factory('User', ['$resource', function($resource) {
    return $resource('/api/users/:id', {}, {
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
    
    $scope.showUsers = function() {
        $location.path('/users');
    }
}]);

planApp.controller('PlanDetailController', ['$scope', '$location', '$routeParams', 'Plan', function($scope, $location, $routeParams, Plan) {
    $scope.plan = Plan.show({id: $routeParams.id});
    
    $scope.cancel = function() {
        $location.path('/list');
    }
    
    $scope.updatePlan = function() {
        Plan.update($scope.plan);
        $location.path('/list');
    }
}]);

planApp.controller('CreatePlanController', ['$scope', '$location', '$routeParams', 'Plans', function($scope, $location, $routeParams, Plans) {
    $scope.plan = {};
    
    $scope.cancel = function() {
        $location.path('/list');
    }
    
    $scope.createPlan = function() {
        Plans.create($scope.plan);
        $location.path('/list');
    }
}]);


planApp.controller('UserListController', ['$scope', '$location', 'Users', function($scope, $location, Users) {
    $scope.users = Users.query(); 
    
    $scope.editUser = function(userId) {
        $location.path('/users/detail/' + userId);
    }
    
    $scope.deleteUser = function(userId) {
        Users.delete({id: userId});
        $scope.users = Users.query();
    }
    
    $scope.createNewUser = function() {
        $location.path('/users/new');
    }
}]);

planApp.controller('UserDetailController', ['$scope', '$location', '$routeParams', 'User', function($scope, $location, $routeParams, User) {
    $scope.user = User.show({id: $routeParams.id});
    
    $scope.cancel = function() {
        $location.path('/users/list');
    }
    
    $scope.updateUser = function() {
        User.update($scope.user);
        $location.path('/users/list');
    }
}]);

planApp.controller('CreateUserController', ['$scope', '$location', '$routeParams', 'Users', function($scope, $location, $routeParams, Users) {
    $scope.user = {};
    
    $scope.cancel = function() {
        $location.path('/users/list');
    }
    
    $scope.createUser = function() {
        Users.create($scope.user);
        $location.path('/users/list');
    }
}]);

planApp.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'app/plan/list.html',
        controller: 'PlanListController'
    }).when('/list', {
        templateUrl: 'app/plan/list.html',
        controller: 'PlanListController'
    }).when('/detail/:id', {
        templateUrl: 'app/plan/detail.html',
        controller: 'PlanDetailController'
    }).when('/new', {
        templateUrl: 'app/plan/new.html',
        controller: 'CreatePlanController'
    }).when('/users', {
        templateUrl: 'app/user/list.html',
        controller: 'UserListController'
    }).when('/users/list', {
        templateUrl: 'app/user/list.html',
        controller: 'UserListController'
    }).when('/users/new', {
        templateUrl: 'app/user/new.html',
        controller: 'CreateUserController'
    }).when('/users/detail/:id', {
        templateUrl: 'app/user/detail.html',
        controller: 'UserDetailController'
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