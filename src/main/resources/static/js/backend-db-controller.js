var mainApp = angular.module("mainApp", []);

mainApp.controller('backendDbController', function($scope, $http) {

    $scope.updateBigDb = function() {
        alert("Starting big db update");
        $http.get('http://newsbuzzwords.com:8080/headlines/updateBigDb').success(function(data) {
            alert("Done (should not be)");
        })
    }

    $scope.updateBuzzDb = function() {
        alert("Starting buzz db update");
        $http.get('http://newsbuzzwords.com:8080/headlines/updateBuzzDb').success(function(data) {
            alert("Done (should not be)");
        })
    }
});