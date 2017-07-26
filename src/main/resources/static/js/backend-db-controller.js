var mainApp = angular.module("mainApp", []);

mainApp.controller('backendDbController', function($scope, $http) {

    $scope.updateBigDb = function() {
        alert("Starting big db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.newsbuzzwords.com/updateBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://newsbuzzwords.com/updateBigDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }

    $scope.updateBuzzDb = function() {
        alert("Starting buzz db update");

        var currentUrl = window.location.href;

        if(currentUrl.includes("www.")) {
            $http.get('http://www.newsbuzzwords.com/updateBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        } else {
            $http.get('http://newsbuzzwords.com/updateBuzzDb').success(function(data) {
                alert("Done (should not be)");
            })
        }
    }
});