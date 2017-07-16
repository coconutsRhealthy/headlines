var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];
    $scope.lastBuzzWord;

    $http.get('http://nieuws-statistieken.nl:8080/headlines-1.0-SNAPSHOT/getBuzzWords').success(function(data) {
        $scope.buzzWords = data;
    })

    $scope.testfunctie = function(word) {
        $scope.words.push(word);
    }

    $scope.loadMoreBuzzWords = function() {
        var sizeBuzzWords = $scope.buzzWords.length - 1;
        $scope.lastBuzzWord = $scope.buzzWords[sizeBuzzWords].word;

        $http.post('/loadMoreBuzzWords', $scope.lastBuzzWord).success(function(data) {
            for(var i = 0; i < data.length; i++) {
                $scope.buzzWords.push(data[i]);
            }
        })
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