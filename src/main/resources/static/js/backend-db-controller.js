var mainApp = angular.module("mainApp", []);

mainApp.controller('backendDbController', function($scope, $http) {

    $scope.updateBigDb = function() {
        alert("Starting big db update");
        $http.get('http://nieuws-statistieken.nl:8080/headlines-1.0-SNAPSHOT/updateBigDb').success(function(data) {
            alert("Done (should not be)");
        })
    }

    $scope.updateBuzzDb = function() {
        alert("Starting buzz db update");
        $http.get('http://nieuws-statistieken.nl:8080/headlines-1.0-SNAPSHOT/updateBuzzDb').success(function(data) {
            alert("Done (should not be)");
        })
    }
});