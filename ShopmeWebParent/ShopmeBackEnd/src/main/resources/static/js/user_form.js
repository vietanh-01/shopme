$(document).ready(function() {
    $("#buttonCancel").on("click", function() {
        window.location = "[[@{/users}]]";
    });

    $("#fileImage").change(function () {
        fileSize = this.files[0].size;
        // alert("File size: " + fileSize)
        if (fileSize > 1048576) {
            this.setCustomValidity("You must choose an image less than 1MB!");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }

    });
});

function  showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        $("#thumbnail").attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
}

function checkEmailUnique(form) {
    url = "[[@{/users/check_email}]]";
    userEmail = $("#email").val();
    userId = $("#id").val();
    csrfValue = $("input[name='_csrf']").val();
    params = {id: userId, email: userEmail, _csrf: csrfValue};

    $.post(url, params, function(response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "Duplicated") {
            showModalDialog("Warning", "There is another user having the email: " + userEmail);
        } else {
            showModalDialog("Error", "Unknown response from server");
        }
    }).fail(function() {
        showModalDialog("Error", "Could not connect to the server");
    });

    return false;
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();

}