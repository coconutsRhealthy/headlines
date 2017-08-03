var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];
    $scope.lastBuzzWord;
    $scope.headline;
    $scope.showMoreButton = false;
    $scope.showPage = false;
    $scope.orderType = "-entry";

    if(window.location.href.includes("www.")) {
        $http.get('http://www.newsbuzzwords.com/getBuzzWords', 3).success(function(data) {
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showMoreButton = true;
            $scope.showPage = true;
        })
    } else {
        $http.get('http://newsbuzzwords.com/getBuzzWords', 3).success(function(data) {
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showMoreButton = true;
            $scope.showPage = true;
        })
    }

    $scope.loadInitialWordsHoursRestriction = function(numberOfHours) {
        if(window.location.href.includes("www.")) {
            $http.post('http://www.newsbuzzwords.com/getBuzzWords', numberOfHours).success(function(data) {
                $scope.buzzWords = data;
            })
        } else {
            $http.post('http://newsbuzzwords.com/getBuzzWords', numberOfHours).success(function(data) {
                $scope.buzzWords = data;
            })
        }
    }

    $scope.testfunctie = function(word) {
        if($scope.words.indexOf(word) !== -1) {
            $scope.words.splice($scope.words.indexOf(word), 1);
        } else {
            $scope.words.push(word);
        }
    }

    $scope.loadMoreBuzzWords = function() {
        var sizeBuzzWords = $scope.buzzWords.length - 1;
        $scope.lastBuzzWord = $scope.buzzWords[sizeBuzzWords].word;

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.post('http://www.newsbuzzwords.com/loadMoreBuzzWords', $scope.lastBuzzWord).success(function(data) {
                for(var i = 0; i < data.length; i++) {
                    $scope.buzzWords.push(data[i]);
                }
            })
        } else {
            $http.post('http://newsbuzzwords.com/loadMoreBuzzWords', $scope.lastBuzzWord).success(function(data) {
                for(var i = 0; i < data.length; i++) {
                    $scope.buzzWords.push(data[i]);
                }
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

    $scope.getBulletColour = function(headlines, bulletNumber) {
        var numberOfHeadlines = headlines.length;
        var stringToReturn;

        if(bulletNumber == 1) {
            stringToReturn = "color:rgb(66, 188, 147)";

        } else if(bulletNumber == 2) {
            if(numberOfHeadlines > 3) {
                stringToReturn = "color:rgb(66, 188, 147)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        } else if(bulletNumber == 3) {
            if(numberOfHeadlines > 5) {
                stringToReturn = "color:rgb(66, 188, 147)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        } else if(bulletNumber == 4) {
            if(numberOfHeadlines > 7) {
                stringToReturn = "color:rgb(66, 188, 147)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        }
        return stringToReturn;
    }

    $scope.changeOrderType = function(type) {
        if($scope.orderType.indexOf(type) === -1) {
            if(type === "entry") {
                $scope.orderType = "-entry";
            } else if(type === "word") {
                $scope.orderType = "word";
            } else if(type === "headlines.length") {
                $scope.orderType = "-headlines.length";
            }
        } else {
            if($scope.orderType.indexOf("-") === -1) {
                $scope.orderType = "-" + $scope.orderType;
            } else {
                $scope.orderType = $scope.orderType.replace('-', '');
            }
        }
    }
});