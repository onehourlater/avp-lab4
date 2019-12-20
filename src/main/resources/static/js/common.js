$(function() {

        $.ajax({
            url:'articles',
            type: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            success: function(response) {

                updateRecordsList(response.body)

            }
         });

         $.ajax({
             url:'subscribers',
             type: 'GET',
             headers: {
                  'Accept': 'application/json',
                  'Content-Type': 'application/json'
              },
             dataType: "json",
             success: function(response) {

                 updateUsersList(response.body)

             }
          });

        $( "#AUTHOR_NAME" ).on('input', function() {
          if($(this).val().length == 0){
            $(this).addClass("input--error");
          }else{
            $(this).removeClass("input--error");
          }
        });

        $( "#EMAIL" ).on('input', function() {
          if($(this).val().length == 0){
            $(this).addClass("input--error");
          }else{
            $(this).removeClass("input--error");
          }
        });

        $( "#BODY" ).on('input', function() {
          if($(this).val().length == 0){
            $(this).addClass("textarea--error");
          }else{
            $(this).removeClass("textarea--error");
          }
        });

        $('#article-send-btn').on('click',function() {
            $(".btn-response").html("")

            // validate inputs

            var errors = false

            if($('#AUTHOR_NAME').val().length == 0){
                $('#AUTHOR_NAME').addClass("input--error");
                errors = true
            }

            if($('#BODY').val().length == 0){
                $('#BODY').addClass("textarea--error");
                errors = true
            }

            if(errors){
                $(".btn-response").html("Пожалуйста заполните все поля")
                return
            }

            // send data

            $.ajax({
                url:'new_article',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify({
                    "authorName": $("#AUTHOR_NAME").val(),
                    "body": $("#BODY").val()
                }),
                type: 'POST',
                dataType: 'json',
                success: function(response) {
                    updateRecordsList(response.body)

                    $("#AUTHOR_NAME").val("")
                    $("#BODY").val("")

                    $(".btn-response").html("Запись успешно размещена")
                }
            });
        });

        $('#subscribe-btn').on('click',function() {
            $(".subscribe-btn-response").html("")

            // validate inputs

            var errors = false

            if($('#EMAIL').val().length == 0){
                $('#EMAIL').addClass("input--error");
                errors = true
            }

            if(errors){
                $(".subscribe-btn-response").html("Пожалуйста введите email")
                return
            }

            // send data

            $.ajax({
                url:'new_subscriber',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify({
                    "email": $("#EMAIL").val(),
                }),
                type: 'POST',
                dataType: 'json',
                success: function(response) {
                    updateUsersList(response.body)

                    $("#EMAIL").val("")

                    $(".subscribe-btn-response").html("Вы успешно подписаны")
                }
            });
        });

        function updateRecordsList(response){
            console.log(response)

            $(".news__list").html("")

            var responseJSON = JSON.parse(response)
            responseJSON.forEach(record => {
                $(".news__list").append(`
                    <div class="article">
                        <div class="article__name">
                            ` + record.authorName + `
                        </div>
                        <div class="article__body">
                            ` + record.body + `
                        </div>
                        <div class="article__date">
                            ` + record.date + `
                        </div>
                    </div>
                `);
            })
        }

        function updateUsersList(response){
            $(".user-cards").html("")

            console.log(response)

            var responseJSON = JSON.parse(response)
            responseJSON.forEach(user => {
                $(".user-cards").append(`
                    <div class="user-cards__card">
                        <span>` + user.id + `</span> ` + user.email + `
                    </div>
                `);
            })
        }
    })