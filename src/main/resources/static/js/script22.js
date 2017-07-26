var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];
    $scope.lastBuzzWord;
    $scope.headline;
    $scope.subHeadline;
    $scope.showMoreButton = false;
    $scope.showPage = false;

    $http.get('http://newsbuzzwords.com:8080/headlines/getBuzzWords').success(function(data) {
        $scope.buzzWords = data;
        $scope.headline = "News Buzzwords";
        $scope.subHeadline = "Trending in news right now:";
        $scope.showMoreButton = true;
        $scope.showPage = true;
    })

    $scope.testfunctie = function(word) {
        $scope.words.push(word);
    }

    $scope.loadMoreBuzzWords = function() {
        var sizeBuzzWords = $scope.buzzWords.length - 1;
        $scope.lastBuzzWord = $scope.buzzWords[sizeBuzzWords].word;

        $http.post('http://newsbuzzwords.com:8080/headlines/loadMoreBuzzWords', $scope.lastBuzzWord).success(function(data) {
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

    $scope.getBulletColour = function(headlines, bulletNumber) {
        var numberOfHeadlines = headlines.length;
        var stringToReturn;

        if(bulletNumber == 1) {
            stringToReturn = "color:rgb(180, 180, 180)";

        } else if(bulletNumber == 2) {
            if(numberOfHeadlines > 3) {
                stringToReturn = "color:rgb(180, 180, 180)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        } else if(bulletNumber == 3) {
            if(numberOfHeadlines > 5) {
                stringToReturn = "color:rgb(180, 180, 180)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        } else if(bulletNumber == 4) {
            if(numberOfHeadlines > 7) {
                stringToReturn = "color:rgb(180, 180, 180)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        }
        return stringToReturn;
    }
});