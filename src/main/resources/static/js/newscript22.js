var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];
    $scope.showMoreButton = false;
    $scope.showPage = false;
    $scope.orderType = "-entry";

    if(window.location.href.includes("finance")) {

    } else if(window.location.href.includes("sport")) {

    } else if(window.location.href.includes("entertainment")) {

    } else {
        $http.post('/getImageBuzzWords').success(function(data) {
            if(data.length === 8) {
                data.pop();
                $scope.showMoreButton = true;
            } else {
                $scope.showMoreButton = false;
            }
            $scope.buzzWords = data;
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

    $scope.loadMoreImageBuzzWords = function() {
        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.post('/loadMoreImageBuzzWords', $scope.buzzWords).success(function(data) {
                if(((data.length - 1) % 7) === 0) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }

                $scope.buzzWords = data;
            })
        } else {
            $http.post('/loadMoreImageBuzzWords', $scope.buzzWords).success(function(data) {
                if(((data.length - 1) % 7) === 0) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }

                $scope.buzzWords = data;
            })
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