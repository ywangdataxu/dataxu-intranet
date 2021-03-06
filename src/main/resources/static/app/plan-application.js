var planApp = angular.module('PlanApplication', ['ngResource', 'ngRoute', 'ui.bootstrap', 'chartjs-directive']);

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

planApp.factory('PlanSchedules', ['$resource', function($resource) {
    return $resource('/api/plans/:id/schedules', {id: '@id'});
}]);

planApp.factory('Users', ['$resource', function($resource) {
    return $resource('/api/users', {}, {
        query: {method: 'GET', isArray: true},
        create: {method: 'POST'},
    });
}]);

planApp.factory('FilterUsers', ['$resource', function($resource) {
    return $resource('/api/users/plan/:id', {}, {
        query: {method: 'GET', isArray: true},
    });
}]);

planApp.factory('User', ['$resource', function($resource) {
    return $resource('/api/users/:id', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {id: '@id'}},
        'delete': {method: 'DELETE', params: {id: '@id'}}
    });
}]);

planApp.factory('Chapters', ['$resource', function($resource) {
    return $resource('/api/chapters', {}, {
        query: {method: 'GET', isArray: true}
    });
}]);

planApp.factory('UserSchedules', ['$resource', function($resource) {
    return $resource('/api/users/:id/schedules', {id: '@id'}, {
        query: {method: 'GET', isArray: true},
        update: {method: 'POST', params: {id: '@id'}}
    });
}]);

planApp.controller('HeaderController', ['$scope', '$location', function($scope, $location) {
    $scope.isPlansMenuActive = function () {
        return $location.path().indexOf('/users') != 0;
    };
    
    $scope.isUsersMenuActive = function() {
        return $location.path().indexOf('/users') == 0;
    }
}]);

planApp.controller('PlanListController', ['$scope', '$location', '$window', 'Plans', function($scope, $location, $window, Plans) {
    $scope.plans = Plans.query(); 
    
    $scope.editPlan = function(planId) {
        $location.path('/detail/' + planId);
    }
    
    $scope.deletePlan = function(planId) {
        var deletePlan = $window.confirm('Are you sure you want to delete this plan?');   
        if (deletePlan) {
            Plans.delete({id: planId}, function() {
                $scope.plans = Plans.query();
            });
        }
    }
    
    $scope.createNewPlan = function() {
        $location.path('/new');
    }
    
    $scope.showUsers = function() {
        $location.path('/users');
    }
    
    
    $scope.showSchedule = function(planId) {
        $location.path('/schedule/' + planId);
    }
}]);

planApp.controller('PlanDetailController', ['$scope', '$location', '$routeParams', 'Plan', 'Users', 'FilterUsers', function($scope, $location, $routeParams, Plan, Users, FilterUsers) {
    $scope.plan = Plan.show({id: $routeParams.id});
    $scope.users = FilterUsers.query({id: $routeParams.id});
    $scope.allowances = [];
    for (var i = 0; i <= 100; i++) {
        $scope.allowances[i] = {name: i + '%', value: i};
    }
    
    $scope.cancel = function() {
        $location.path('/list');
    }
    
    $scope.updatePlan = function() {
        for (var i = 0; i < $scope.users.length; i++) {
            var currUser = $scope.users[i];
            var checked = document.getElementById('user_id_' + currUser.id).checked;
            if (checked) {
                var chapterId =  $("input[type='radio'][name='contact_chapter_" + currUser.id + "']:checked").val();
                if (!chapterId) {
                    alert("Please select chapter for " + currUser.first_name + " " + currUser.last_name);
                    return false;
                }
            }
        }

        for (var i = 0; i < $scope.users.length; i++) {
            var currUser = $scope.users[i];
            var checked = document.getElementById('user_id_' + currUser.id).checked;
            if (checked) {
                var chapterId =  $("input[type='radio'][name='contact_chapter_" + currUser.id + "']:checked").val();
                $scope.plan.plan_contacts.push({contact:currUser, chapter_id: chapterId, plan_id: $scope.plan.id});
            }
        }
        
        for (var i = 0; i < $scope.plan.plan_contacts.length; i++) {
            var currUser = $scope.plan.plan_contacts[i].contact;
            var checked = document.getElementById('user_id_' + currUser.id).checked;
            if (!checked) {
                $scope.plan.plan_contacts.splice(i, 1);
            }
        }
        
        Plan.update($scope.plan, function() {
            $location.path('/list');
        });
    }

    $scope.toggleEngineers = function() {
        var checked = document.getElementById('user_select_all').checked;
        for (var i = 0; i < $scope.users.length; i++) {
            var currUser = $scope.users[i];
            document.getElementById('user_id_' + currUser.id).checked = checked;
        }
        
        for (var i = 0; i < $scope.plan.plan_contacts.length; i++) {
            var currUser = $scope.plan.plan_contacts[i].contact;
            document.getElementById('user_id_' + currUser.id).checked = checked;
        }
    }
    
    $scope.showSchedule = function() {
        $location.path('/schedule/' + $scope.plan.id);
    }
}]);

planApp.controller('PlanScheduleController', ['$scope', '$location', '$routeParams', 'Plan', 'Users', 'PlanSchedules', 'UserSchedules',
                                              function($scope, $location, $routeParams, Plan, Users, PlanSchedules, UserSchedules) {
    $scope.chartWidth = 1200;
    $scope.chartHeight = 300;
    $scope.plan = Plan.show({id: $routeParams.id});
    $scope.myChart;
    $scope.legend = [];
    $scope.data = {};
    $scope.chapter = ['LS', 'RTS', 'RWH', 'UI'];
    $scope.showDetails = false;
    $scope.detailToggleText = 'Show Plan Details';
    $scope.showChartSettings = false;
    $scope.chartTypes = ['accumulated', 'normal'];
    $scope.chartType = 'accumulated';
    $scope.currUserId = -1;
    $scope.currUserSchedule;
    $scope.allowances = [];
    for (var i = 0; i <= 100; i++) {
        $scope.allowances[i] = {name: i + '%', value: i};
    }
    
    var colors = ["orange", "green", "grey", "red", "pink", "black", "yello", "purple"];
    var ctx = document.getElementById("myChart").getContext("2d");
    
    $scope.updateSchedule = function() {
        Plan.update($scope.plan, function() {
            $scope.myChart.destroy();
            $scope.updateScheduleChart(true);
        });
    }
    
    $scope.updateScheduleChart = function(update) {
        $scope.schedules = PlanSchedules.get({id: $routeParams.id, chartType: $scope.chartType}, function() {
            $scope.legend = [];
            var dataSet = [];
            for (var i = 0; i < $scope.schedules.data_set.length; i++) {
                $scope.legend.push({name: $scope.schedules.data_set[i].chapter_name, color: colors[i % colors.length]});
                
                for (var j = 0; j < $scope.schedules.data_set[i].data.length; j++) {
                    $scope.schedules.data_set[i].data[j] = $scope.schedules.data_set[i].data[j].toFixed(1);
                }
                
                dataSet.push({
                        fillColor: colors[i % colors.length],
                        data: $scope.schedules.data_set[i].data
                });
            }
            $scope.data = {
                    labels: $scope.schedules.dates,
                    datasets: dataSet
            };
           
            ctx.canvas.width = $scope.chartWidth;
            ctx.canvas.height = $scope.chartHeight;
            
            $scope.myChart = new Chart(ctx).Bar($scope.data);
        });
    }
    
    $scope.updateScheduleChart(false);
    
    $scope.toggleDetails = function() {
        $scope.showDetails = !$scope.showDetails;
        if ($scope.showDetails) {
            $scope.detailToggleText = 'Hide Plan Details';
        } else {
            $scope.detailToggleText = 'Show Plan Details';
        }
    }
    
    $scope.toggleChartSettings = function() {
        $scope.showChartSettings = !$scope.showChartSettings;
    }
    
    $scope.updateChart = function() {
        $scope.chartWidth = $('#chartWidth').val();
        $scope.chartHeight = $('#chartHeight').val();
        $scope.showChartSettings = false;
        $scope.updateSchedule();
    }
    
    $scope.showUserSchedule = function(userId) {
        $scope.currUserId = userId;
        
        if ($scope.currUserId != -1) {
            $scope.currUserSchedule = UserSchedules.query({id: userId, planId: $scope.plan.id});
        }
    }
    
    $scope.userSchedule = function(userId) {
        return $scope.currUserId == userId;
    }
}]);

planApp.controller('CreatePlanController', ['$scope', '$location', '$routeParams', 'Plans', function($scope, $location, $routeParams, Plans) {
    $scope.plan = {};
    $scope.allowances = [];
    for (var i = 0; i <= 100; i++) {
        $scope.allowances[i] = {name: i + '%', value: i};
    }
    
    $scope.cancel = function() {
        $location.path('/list');
    }
    
    $scope.createPlan = function() {
        Plans.create($scope.plan);
        $location.path('/list');
    }
}]);


planApp.controller('UserListController', ['$scope', '$location', '$window', 'Users', function($scope, $location, $window, Users) {
    $scope.users = Users.query(); 
    
    $scope.editUser = function(userId) {
        $location.path('/users/detail/' + userId);
    }
    
    $scope.editUserSchedule = function(userId) {
        $location.path('/users/schedule/' + userId);
    }
    
    $scope.deleteUser = function(userId) {
        var deleteUser = $window.confirm('Are you sure you want to delete this user?');   
        if (deleteUser) {
            Users.delete({id: userId});
            $scope.users = Users.query();
        }
    }
    
    $scope.createNewUser = function() {
        $location.path('/users/new');
    }
}]);

planApp.controller('UserDetailController', ['$scope', '$location', '$routeParams', 'User', 'Chapters', function($scope, $location, $routeParams, User, Chapters) {
    $scope.user = User.show({id: $routeParams.id});
    $scope.chapters = Chapters.query();
    
    $scope.cancel = function() {
        $location.path('/users/list');
    }
    
    $scope.updateUser = function() {
        User.update($scope.user);
        $location.path('/users/list');
    }
    
    $scope.editUserSchedule = function() {
        $location.path('/users/schedule/' + $scope.user.id);
    }
}]);

planApp.controller('UserScheduleController', ['$scope', '$location', '$routeParams', '$window', 'User', 'UserSchedules', function($scope, $location, $routeParams, $window, User, UserSchedules) {
    $scope.user = User.show({id: $routeParams.id});
    $scope.schedules = UserSchedules.query({id: $routeParams.id});
    var scheduleComparator = function(a, b) {
        a = new Date(a.start_date);
        b = new Date(b.start_date);
        if (a < b) {
            return -1;
         } else {
             return 1;
         }
    };
    
    $scope.cancel = function() {
        $location.path('/users');
    }
    
    $scope.deleteSchedule = function(scheduleId) {
        for (var i = 0; i < $scope.schedules.length; i++) {
            if (scheduleId == $scope.schedules[i].id) {
                $scope.schedules.splice(i, 1);
                break;
            }
        }
    }
    
    $scope.addNewSchedule = function() {
        $scope.schedules.push({contact_id: $scope.user.id});
    }
    
    $scope.sortSchedules = function() {
        $scope.schedules.sort(scheduleComparator);
    }
    
    $scope.updateSchedule = function() {
        for (var i = 0; i < $scope.schedules.length; i++) {
            if (new Date($scope.schedules[i].start_date) > new Date($scope.schedules[i].end_date)) {
                $window.alert($scope.schedules[i].reason + " start date is greater than the end date");
                return;
            }
        }
        
        UserSchedules.update({id: $scope.user.id}, $scope.schedules);
        $location.path('/users');
    }
}]);
planApp.controller('CreateUserController', ['$scope', '$location', '$routeParams', 'Users', 'Chapters', function($scope, $location, $routeParams, Users, Chapters) {
    // I know it is stupid.
    $scope.user = {velocities: [{chapter: {name: 'LS', id: 1}, velocity: 0},
                                {chapter: {name: 'RTS', id: 2}, velocity: 0},
                                {chapter: {name: 'RWH', id: 3}, velocity: 0},
                                {chapter: {name: 'UI', id: 4}, velocity: 0}
                                ]};
    $scope.chapters = Chapters.query();
    
    $scope.cancel = function() {
        $location.path('/users/list');
    }
    
    $scope.createUser = function(formObj) {
        Users.create($scope.user, function() {
            $location.path('/users/list');
        });
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
    }).when('/schedule/:id', {
        templateUrl: 'app/plan/schedule.html',
        controller: 'PlanScheduleController'
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
    }).when('/users/schedule/:id', {
        templateUrl: 'app/user/schedule.html',
        controller: 'UserScheduleController'
    }).when('/line', {
        templateUrl: 'app/plan/line.html'
    }).when('/bar', {
        templateUrl: 'app/plan/bar.html'
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


