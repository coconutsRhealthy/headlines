var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.buzzWords;
    $scope.words = [];
    $scope.headline;
    $scope.showMoreButton = false;
    $scope.showPage = false;
    $scope.orderType = "-headlines.length";
    $scope.numberOfHoursToShow = 3;

    $scope.hour3buttonclass = "btn btn-default btn-xs active";
    $scope.hour6buttonclass = "btn btn-default btn-xs";
    $scope.hour12buttonclass = "btn btn-default btn-xs";
    $scope.hour24buttonclass = "btn btn-default btn-xs";

    if(window.location.href.includes("finance")) {
        $http.post('/getFinanceBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
            if(data.length === 21) {
                data.pop();
                $scope.showMoreButton = true;
            } else {
                $scope.showMoreButton = false;
            }
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showPage = true;
        })
    } else if(window.location.href.includes("sport")) {
        $http.post('/getSportBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
            if(data.length === 21) {
                data.pop();
                $scope.showMoreButton = true;
            } else {
                $scope.showMoreButton = false;
            }
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showPage = true;
        })
    } else if(window.location.href.includes("entertainment")) {
        $http.post('/getEntertainmentBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
            if(data.length === 21) {
                data.pop();
                $scope.showMoreButton = true;
            } else {
                $scope.showMoreButton = false;
            }
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showPage = true;
        })
    } else if(window.location.href.includes("crypto")) {
        $http.post('/getCryptoBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
            if(data.length === 21) {
                data.pop();
                $scope.showMoreButton = true;
            } else {
                $scope.showMoreButton = false;
            }
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showPage = true;
        })
    } else {
        $http.post('/getBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
            if(data.length === 21) {
                data.pop();
                $scope.showMoreButton = true;
            } else {
                $scope.showMoreButton = false;
            }
            $scope.buzzWords = data;
            $scope.headline = "News Buzzwords";
            $scope.showPage = true;
        })
    }

    $scope.loadInitialWordsHoursRestriction = function(numberOfHours) {
        setActiveButtonClass(numberOfHours);

        if(window.location.href.includes("finance")) {
            $http.post('/getFinanceBuzzWords', numberOfHours).success(function(data) {
                if(data.length === 21) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }
                $scope.buzzWords = data;
                $scope.numberOfHoursToShow = numberOfHours;
            })
        } else if(window.location.href.includes("sport")) {
            $http.post('/getSportBuzzWords', numberOfHours).success(function(data) {
                if(data.length === 21) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }
                $scope.buzzWords = data;
                $scope.numberOfHoursToShow = numberOfHours;
            })
        } else if(window.location.href.includes("entertainment")) {
            $http.post('/getEntertainmentBuzzWords', numberOfHours).success(function(data) {
                if(data.length === 21) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }
                $scope.buzzWords = data;
                $scope.numberOfHoursToShow = numberOfHours;
            })
        } else if(window.location.href.includes("crypto")) {
            $http.post('/getCryptoBuzzWords', $scope.numberOfHoursToShow).success(function(data) {
                if(data.length === 21) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }
                $scope.buzzWords = data;
                $scope.headline = "News Buzzwords";
                $scope.showPage = true;
            })
        } else {
            $http.post('/getBuzzWords', numberOfHours).success(function(data) {
                if(data.length === 21) {
                    data.pop();
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }
                $scope.buzzWords = data;
                $scope.numberOfHoursToShow = numberOfHours;
            })
        }
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

    $scope.loadMoreBuzzWords = function() {
        var currentUrl = window.location.href;

        var combinedDataToSend = $scope.numberOfHoursToShow + " ---- " + $scope.buzzWords.length;

        if(currentUrl.includes("finance")) {
            $http.post('/loadMoreFinanceBuzzWords', combinedDataToSend).success(function(data) {
                data.pop();
                if(data.length % 20 === 0) {
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }

                $scope.buzzWords = data;
            })
        } else if(currentUrl.includes("sport")) {
            $http.post('/loadMoreSportBuzzWords', combinedDataToSend).success(function(data) {
                data.pop();
                if(data.length % 20 === 0) {
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }

                $scope.buzzWords = data;
            })
        } else if(currentUrl.includes("entertainment")) {
            $http.post('/loadMoreEntertainmentBuzzWords', combinedDataToSend).success(function(data) {
                data.pop();
                if(data.length % 20 === 0) {
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }

                $scope.buzzWords = data;
            })
        } else if(currentUrl.includes("crypto")) {
            $http.post('/loadMoreCryptoBuzzWords', combinedDataToSend).success(function(data) {
                data.pop();
                if(data.length % 20 === 0) {
                    $scope.showMoreButton = true;
                } else {
                    $scope.showMoreButton = false;
                }

                $scope.buzzWords = data;
            })
        } else {
            $http.post('/loadMoreBuzzWords', combinedDataToSend).success(function(data) {
                data.pop();
                if(data.length % 20 === 0) {
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