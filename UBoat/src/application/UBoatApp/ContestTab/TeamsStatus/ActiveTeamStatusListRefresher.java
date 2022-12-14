package application.UBoatApp.ContestTab.TeamsStatus;


import UBoatDTO.ActiveTeamsDTO;
import application.http.HttpClientAdapter;
import general.HttpResponseDTO;
import http.client.CustomHttpClient;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static application.UBoatApp.UBoatAppController.createErrorAlertWindow;
import static general.ConstantsHTTP.ACTIVE_TEAMS_LIST;
import static java.net.HttpURLConnection.HTTP_OK;

;

public class ActiveTeamStatusListRefresher extends TimerTask {


    private final Consumer<ActiveTeamsDTO> activeTeamsConsumer;
    private final Consumer<ActiveTeamsDTO> enableReadyButton;
    private final CustomHttpClient httpClientUtil;
    private final AtomicInteger counter=new AtomicInteger(0);
    public ActiveTeamStatusListRefresher(Consumer<ActiveTeamsDTO> activeTeamsConsumer,Consumer<ActiveTeamsDTO> enableReadyButton) {

        this.activeTeamsConsumer = activeTeamsConsumer;
        this.enableReadyButton = enableReadyButton;
        this.httpClientUtil = HttpClientAdapter.getHttpClient();
    }

    @Override
    public void run() {
    System.out.println(counter.getAndIncrement()+"#Sending active teams allies request to server....");
    HttpResponseDTO responseDTO=httpClientUtil.doGetSync(ACTIVE_TEAMS_LIST);

        if (responseDTO.getBody() != null && !responseDTO.getBody().isEmpty()) {
        if(responseDTO.getCode()==HTTP_OK) {
            ActiveTeamsDTO activeTeamsDTO=httpClientUtil.getGson().fromJson(responseDTO.getBody(),ActiveTeamsDTO.class);
            activeTeamsConsumer.accept(activeTeamsDTO);
            enableReadyButton.accept(activeTeamsDTO);}
        else
            createErrorAlertWindow("Contest Lower Side Update",responseDTO.getBody());
    }else
    createErrorAlertWindow("Contest Lower Side Update","General error");

    }

}
