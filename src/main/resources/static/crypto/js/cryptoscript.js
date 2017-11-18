var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];
    $scope.headline;
    $scope.showPage = false;
    $scope.orderType = "-headlines.length";
    $scope.numberOfHoursToShow = 3;

    $scope.hour3buttonclass = "btn btn-default btn-xs active";
    $scope.hour6buttonclass = "btn btn-default btn-xs";
    $scope.hour12buttonclass = "btn btn-default btn-xs";
    $scope.hour24buttonclass = "btn btn-default btn-xs";

    $http.post('/getCryptoBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
        $scope.buzzWords = data;
        $scope.headline = "News Buzzwords";
        $scope.showPage = true;
    })

    $scope.loadInitialWordsHoursRestriction = function(numberOfHours) {
        setActiveButtonClass(numberOfHours);

        $http.post('/getCryptoBuzzWords', numberOfHours).success(function(data) {
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showPage = true;
        })
    }

    function setActiveButtonClass(numberOfHours) {
        if(numberOfHours === 3) {
            $scope.hour3buttonclass = "btn btn-default btn-xs active";
            $scope.hour6buttonclass = "btn btn-default btn-xs";
            $scope.hour12buttonclass = "btn btn-default btn-xs";
            $scope.hour24buttonclass = "btn btn-default btn-xs";
        } else if(numberOfHours === 6) {
            $scope.hour3buttonclass = "btn btn-default btn-xs";
            $scope.hour6buttonclass = "btn btn-default btn-xs active";
            $scope.hour12buttonclass = "btn btn-default btn-xs";
            $scope.hour24buttonclass = "btn btn-default btn-xs";
        } else if(numberOfHours === 12) {
            $scope.hour3buttonclass = "btn btn-default btn-xs";
            $scope.hour6buttonclass = "btn btn-default btn-xs";
            $scope.hour12buttonclass = "btn btn-default btn-xs active";
            $scope.hour24buttonclass = "btn btn-default btn-xs";
        } else if(numberOfHours === 24) {
            $scope.hour3buttonclass = "btn btn-default btn-xs";
            $scope.hour6buttonclass = "btn btn-default btn-xs";
            $scope.hour12buttonclass = "btn btn-default btn-xs";
            $scope.hour24buttonclass = "btn btn-default btn-xs active";
        }
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
            if(numberOfHeadlines > 4) {
                stringToReturn = "color:rgb(66, 188, 147)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        } else if(bulletNumber == 4) {
            if(numberOfHeadlines > 5) {
                stringToReturn = "color:rgb(66, 188, 147)";
            } else {
                stringToReturn = "color:rgb(225, 225, 225)";
            }
        }
        return stringToReturn;
    }

    $scope.getGroupColour = function(group) {
        var stringToReturn;

        switch(group) {
            case 1:
                stringToReturn = "color:rgb(0, 0, 128)";
                break;
            case 2:
                stringToReturn = "color:rgb(174, 174, 255)";
                break;
            case 3:
                stringToReturn = "color:rgb(255, 183, 183)";
                break;
            case 4:
                stringToReturn = "color:rgb(151, 151, 0)";
                break;
            default:
                stringToReturn = "color:rgb(255, 255, 255)";
                break;
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
            } else if(type === "group") {
                $scope.orderType = "group";
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