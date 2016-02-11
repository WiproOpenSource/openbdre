var progressBarUsed = false,
    nanobar = new Nanobar({
        bg: '#337ab7',
        target: $('#nanobardiv')[0],
        id: 'mynano'
    }),
    loadProgressBar = function (progressPercent) {
        if (progressPercent >= 100) {
            nanobar.go(100);
            $('#modalCover').fadeOut(900);
            progressBarUsed = false;
        }
        else {
            if (progressBarUsed && progressPercent > progressBarUsed) {
                nanobar.go(progressPercent);
                progressBarUsed = progressPercent;
            }
            else if (progressBarUsed && progressPercent < progressBarUsed) {
                console.log('give some progress to progress bar');
            }
            else {
                $('#modalCover').fadeIn(0);
                progressBarUsed = 1;
                loadProgressBar(progressPercent);
            }
        }
}

var goToPage = function (pageName) {
    switch(pageName) {
        case 'wfdesigner-page':
            location.href = '/mdui/pages/wfdesigner.page';
            break;
        case 'process-page':
            location.href = '/mdui/pages/process.page';
            break;
        default:
            console.log('pageName not found');
    }
}

var alertBox = function (alerttype, message) {
	var alertType = '',
	    alertMessage = {};
    switch(alerttype) {
        case 'success':
            alertType = 'alert alert-success';
            alertMessage = {
                'name' : 'Success!',
                'text' : message
            }
            break;
        case 'info':
            alertType = 'alert alert-info';
            alertMessage = {
                'name' : 'Info!',
                'text' : message
            }
            break;
        case 'warning':
            alertType = 'alert alert-warning';
            alertMessage = {
                'name' : 'Warning!',
                'text' : message
            }
            break;
        case 'danger':
            alertType = 'alert alert-danger';
            alertMessage = {
                'name' : 'Danger!',
                'text' : message
            }
            break;
        default:
            alertType = 'alert alert-success';
            alertMessage = {
                'name' : 'Default Boring Message',
                'text' : 'Do not Bore Me!'
            }
        }

        $('#alertBox').removeClass();
        $('#alertBox').addClass(alertType);
        var temp = $('#alertBox').html(alertMessage.text).hide();
        temp.fadeIn(1000);
}
