var mainApp = angular.module("mainApp", []);

mainApp.controller('topicsController', function($scope, $http) {

    $scope.topics;
    $scope.words = [];
    $scope.showPage = false;
    $scope.orderType = "-entry";

    if(window.location.href.includes("business")) {
        $http.post('/getFinanceTopics').success(function(data) {
            $scope.topics = data;
            $scope.showPage = true;
        })
    } else if(window.location.href.includes("sports")) {
        $http.post('/getSportTopics').success(function(data) {
            $scope.topics = data;
            $scope.showPage = true;
        })
    } else if(window.location.href.includes("entertainment")) {
        $http.post('/getEntertainmentTopics').success(function(data) {
            $scope.topics = data;
            $scope.showPage = true;
        })
    } else {
        $http.post('/getWorldNewsTopics').success(function(data) {
            $scope.topics = data;
            $scope.showPage = true;
        })
    }

    $scope.testfunctie = function(word) {
        if($scope.words.indexOf(word) !== -1) {
            $scope.words.splice($scope.words.indexOf(word), 1);
        } else {
            $scope.words.push(word);
        }
    }

    $scope.check = function(word) {
        for (var i = 0; i < $scope.words.length; i++) {
            if ($scope.words[i] == word) {
                return true;
            }
        }
        return false;
    }
});