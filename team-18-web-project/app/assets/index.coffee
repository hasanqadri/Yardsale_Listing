$ ->
  $.get "/user", (users) ->
    $.each users, (index, users) ->
      $('#users').append $("<li>").text users.name