var mainApp = angular.module("mainApp", []);

mainApp.controller('backendDbController', function($scope, $http) {

    $scope.updateBigDb = function() {
        alert("Starting big db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateBuzzDb = function() {
        alert("Starting buzz db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateFinanceBigDb = function() {
        alert("Starting finance big db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateFinanceBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateFinanceBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateFinanceBuzzDb = function() {
        alert("Starting finance buzz db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateFinanceBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateFinanceBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateSportBigDb = function() {
        alert("Starting sport big db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateSportBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateSportBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateSportBuzzDb = function() {
        alert("Starting sport buzz db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateSportBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateSportBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateEntertainmentBigDb = function() {
        alert("Starting entertainment big db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateEntertainmentBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateEntertainmentBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateEntertainmentBuzzDb = function() {
        alert("Starting entertainment buzz db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateEntertainmentBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateEntertainmentBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateCryptoBigDb = function() {
        alert("Starting crypto big db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateCryptoBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateCryptoBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateCryptoBuzzDb = function() {
        alert("Starting crypto buzz db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/updateCryptoBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/updateCryptoBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.postTweets = function() {
        alert("Starting tweet posting");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.headl1nes.com/postTweets').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://headl1nes.com/postTweets').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

        $scope.postCryptoTweets = function() {
            alert("Starting crypto tweet posting");

            var currentUrl = window.location.href;

            if(currentUrl.includes("www.")) {
                $http.get('http://www.headl1nes.com/postCryptoTweets').success(function(data) {
                    alert("Done (should not be)");
                })
            } else {
                $http.get('http://headl1nes.com/postCryptoTweets').success(function(data) {
                    alert("Done (should not be)");
                })
            }
        }
});