var mainApp = angular.module("mainApp", []);

mainApp.controller('bigDbController', function($scope, $http) {

    $scope.updateBigDb = function() {
        alert("Starting big db update");
        $http.get('http://nieuws-statistieken.nl:8080/headlines-1.0-SNAPSHOT/updateBigDb').success(function(data) {
            alert("Done (should not be)");
        })
    }
});