var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];

    $http.get('http://nieuws-statistieken.nl:8080/headlines-1.0-SNAPSHOT/getBuzzWords').success(function(data) {
        $scope.buzzWords = data;
    })

    $scope.testfunctie = function(word) {
        $scope.words.push(word);
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