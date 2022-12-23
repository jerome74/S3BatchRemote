package com.enel.s3mock.gui;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.enel.s3mock.model.*;
import com.enel.s3mock.service.ServiceDownloadS3FilesImpl;
import com.enel.s3mock.util.CountyLevel;
import com.enel.s3mock.util.PropertyParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.enel.s3mock.util.IEntitiesUtil.*;

public class ParquetsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel entity;
    private JComboBox comboEntity;
    private JComboBox comboCountry;
    private JComboBox comboLevel;
    private JTextPane displayLabel;
    private JLabel ENTITYLabel;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JScrollPane displayLabelScroll;
    private JComboBox comboBox3;
    private JCheckBox checkBox1;
    private JScrollPane TableScroll;
    private JTable tableDisplay;
    private JEditorPane ediFileName;
    private JButton S3Button;
    private JButton S3DisplayButton;
    private JCheckBox checkAllCountries;
    private JCheckBox checkExtraction;
    private JTable table1;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ServiceDownloadS3FilesImpl serviceDownloadS3Files = new ServiceDownloadS3FilesImpl();

    private java.util.List<String> LEVEL = java.util.List.of("m", "l", "h");
    private java.util.List<String> COUNTRY = List.of("italy", "espana", "colombia", "chile", "peru", "romania", "saopaulo", "goias", "ceras", "rio");

    public ParquetsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        var verticalBar = displayLabelScroll.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);

        displayLabel.setCaretPosition(displayLabel.getDocument().getLength());

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        S3Button.addActionListener((ActionEvent e) -> {
            JFileChooser fc = new JFileChooser("src/test/resources/");
            int result = fc.showSaveDialog(getRootPane());
            final var selectedFile = fc.getSelectedFile();
            ediFileName.setText(selectedFile.getAbsolutePath());

        });

        S3DisplayButton.addActionListener((ActionEvent e) -> {


            Path path = Paths.get(ediFileName.getText());
            var jtable = (JTable) TableScroll.getViewport().getView();

            serviceDownloadS3Files.displayFile(path, jtable, Integer.parseInt(comboBox2.getSelectedItem().toString()), new StringBuffer());

        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {


        String[] columnNames = {"First Name", "Last Name"};
        Object[][] data = {{"Kathy", "Smith"}, {"John", "Doe"}};
        table1 = new JTable(data, columnNames);
        table1.setFillsViewportHeight(true);

        AtomicReference<String> entitySelectString = new AtomicReference<String>();
        AtomicReference<String> countrySelectString = new AtomicReference<String>();
        AtomicReference<String> levelSelectString = new AtomicReference<String>();
        AtomicReference<String> limitSelectString = new AtomicReference<String>();
        AtomicReference<String> rowSelectString = new AtomicReference<String>();
        AtomicReference<String> envSelectString = new AtomicReference<String>();
        AtomicReference<JTextPane> arJTextPane = new AtomicReference<JTextPane>();
        AtomicReference<Boolean> checkDelete = new AtomicReference<Boolean>();
        AtomicReference<String> snapshotString = new AtomicReference<String>();
        AtomicReference<JScrollPane> arJScrollPane = new AtomicReference<JScrollPane>();
        AtomicReference<JScrollPane> tabJScrollPane = new AtomicReference<JScrollPane>();
        AtomicReference<JTable> arJTable = new AtomicReference<JTable>();

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JComboBox.class) && ((JComboBox<?>) component).getName().equals("comboEntity"))
                entitySelectString.set(Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem()).toString().toLowerCase());
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JComboBox.class) && ((JComboBox<?>) component).getName().equals("comboCountry"))
                countrySelectString.set(Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem()).toString().toLowerCase());
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JComboBox.class) && ((JComboBox<?>) component).getName().equals("comboLevel")) {
                levelSelectString.set(Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem()).toString().toLowerCase());
                if (levelSelectString.get().equals("empty"))
                    levelSelectString.set("");
            }
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JComboBox.class) && ((JComboBox<?>) component).getName().equals("comboFile"))
                limitSelectString.set(Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem()).toString());
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JComboBox.class) && ((JComboBox<?>) component).getName().equals("comboRow"))
                rowSelectString.set(Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem()).toString());
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JComboBox.class) && ((JComboBox<?>) component).getName().equals("comboEnv"))
                envSelectString.set(Objects.requireNonNull(((JComboBox<?>) component).getSelectedItem()).toString().toLowerCase());
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JScrollPane.class) && ((JScrollPane) component).getName().equals("displayLabelScroll"))

                arJTextPane.set((JTextPane) ((JScrollPane) component).getViewport().getView());
        });

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JScrollPane.class) && ((JScrollPane) component).getName().equals("TableScroll")) {

                arJTable.set((JTable) ((JScrollPane) component).getViewport().getView());

            }
        });

        var logInfo = "[".concat(entitySelectString.get().toUpperCase(Locale.ROOT)).concat("] [").concat(countrySelectString.get().toUpperCase(Locale.ROOT)).concat("] [").concat(levelSelectString.get().toUpperCase(Locale.ROOT)).concat("]");

        /*
        DELETE
         */

        Arrays.stream(entity.getComponents()).forEach(component -> {
            if (component.getClass().equals(JCheckBox.class) && ((JCheckBox) component).getName().equals("checkDelete"))
                checkDelete.set(((JCheckBox) component).isSelected());
        });


        var eleEntity = ENTITIES.stream().filter(entity1 -> entity1.getName().equals(entitySelectString.get())).collect(Collectors.toList()).stream().findFirst().get();


        if (checkExtraction.isSelected()) {
            executeTriggeer(envSelectString.get().toLowerCase()
                    , comboEntity.getSelectedItem().toString().toLowerCase()
                    , comboLevel.getSelectedItem().toString().toLowerCase()
                    , comboCountry.getSelectedItem().toString().toLowerCase()
                    , arJTextPane.get()
                    , checkAllCountries.isSelected());
        } else {


            listingObjectsTest(entitySelectString.get().concat(countrySelectString.get()).concat(levelSelectString.get())
                    , eleEntity.getMsName()
                    , eleEntity.getMsNumber()
                    , arJTextPane.get()
                    , Integer.parseInt(limitSelectString.get())
                    , Integer.parseInt(rowSelectString.get())
                    , logInfo
                    , envSelectString.get()
                    , checkDelete.get()
                    , arJTable.get());
        }
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        System.setProperty("hadoop.home.dir", "/");
        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.lightGray);
        ParquetsDialog dialog = new ParquetsDialog();
        dialog.setSize(1024, 768);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        dialog.setAlwaysOnTop(true);
        //dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private static final Regions clientRegion = Regions.EU_CENTRAL_1;
    private static final String PROPERTIES_FILE = "application.properties";

    /**
     * @param enviroment
     * @param entityNameExtract
     * @param levelValue
     * @param countryValue
     * @param jTextPane
     * @param allCountries
     */
    public void executeTriggeer(String enviroment, String entityNameExtract, String levelValue, String countryValue, JTextPane jTextPane, boolean allCountries) {

        var prop = new PropertyParser();

        var response = new StringBuffer();

        try {
            prop.load(ParquetsDialog.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebClient webClient = WebClient.create().mutate().build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", "RAP350891002");
        formData.add("password", "Dd3VTAM?z#tbvcpf");

        var pathToken = prop.getProperty("ms.path.token.".concat(enviroment));
        var bsTriggerPath = prop.getProperty("bs.trigger.".concat(enviroment));
        var basicAuth = prop.getProperty("basicauth.".concat(enviroment));


        webClient.post().uri(pathToken).header("Authorization", "Basic ".concat(basicAuth))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData)).retrieve().bodyToMono(ResponseToken.class).switchIfEmpty(Mono.error(() -> new Exception("Empty Body to retreive token")))
                .log().subscribe(responseToken -> {

                    var countryLevel = new CountyLevel();


                    var country = Optional.of(COUNTRY).filter(field -> allCountries)
                            .map(field -> COUNTRY)
                            .or(() -> Optional.of(COUNTRY).map(strings -> COUNTRY.stream().filter(s -> s.equals(countryValue)).collect(Collectors.toList()))).get();


                    var levels = Optional.of(LEVEL).filter(field -> !levelValue.equals("empty"))
                            .map(field -> LEVEL)
                            .or(() -> Optional.of(LEVEL).map(strings -> LEVEL.stream().filter(s -> s.equals("no value")).collect(Collectors.toList()))).get();


                    var countryLevels = country.stream().flatMap(s -> levels.stream().map(s::concat)).collect(Collectors.toList());

                    Flux.fromIterable(countryLevels).distinctUntilChanged().subscribe(clevel -> {

                        var entity = ENTITIES.stream().filter(entity1 -> entity1.getName().equals(entityNameExtract)).findFirst().orElse(new Entity());

                        var requestTrigger = RequestTrigger.builder()
                                .version(entity.getVersion())
                                .sparkEntity(entity.getSparkEntity())
                                .endpoint(entity.getEndpoint())
                                .extractor(entity.getExtractor()).build();

                        requestTrigger.setEntity(entityNameExtract.concat(clevel));

                        response.append("_______________________________").append(System.getProperty("line.separator"));
                        response.append("Entity :: ").append("[ ").append(requestTrigger.getEntity().toUpperCase()).append(" ] :: [ ").append(enviroment.toUpperCase()).append(" ]").append(System.getProperty("line.separator"));
                        response.append("_______________________________").append(System.getProperty("line.separator"));

                        webClient
                                .post()
                                .uri(bsTriggerPath)
                                .header("Authorization", "Bearer ".concat(responseToken.getAccessToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(requestTrigger)
                                .retrieve()
                                .bodyToMono(ResponseBsTrigger.class)
                                .switchIfEmpty(Mono.error(() -> new Exception("Empty Body to retreive bs trigger")))
                                .log()
                                .subscribe(bsTriggerResp -> {

                                    AtomicBoolean continueFlag = new AtomicBoolean(true);

                                    var pathLivy = prop.getProperty("livy.path.".concat(enviroment)).concat(bsTriggerResp.getOutput().getJobId());

                                    webClient.get()
                                            .uri(pathLivy)
                                            .retrieve()
                                            .bodyToMono(String.class)
                                            .concatWith(Mono.delay(Duration.ofSeconds(10)).then().cast(String.class))
                                            .log()
                                            .repeat(continueFlag::get)
                                            .subscribe(buffer -> {

                                                String jsonString = buffer.toString();
                                                SessionLivy session = null;
                                                try {
                                                    session = objectMapper.readValue(jsonString, SessionLivy.class);
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                switch (session.state) {
                                                    case "success":
                                                    case "dead":
                                                        continueFlag.set(Boolean.FALSE);
                                                        response.append("[ ").append(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date())).append(" ] ")
                                                                .append(requestTrigger.getEntity())
                                                                .append(" ( jobId = ").append(bsTriggerResp.getOutput().getJobId()).append(" )")
                                                                .append(" -> [ ").append(session.state.toUpperCase()).append(" ]")
                                                                .append(System.getProperty("line.separator"));
                                                        break;
                                                    default:
                                                        continueFlag.set(Boolean.TRUE);
                                                        response.append("[ ").append(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(new Date())).append(" ] ")
                                                                .append(requestTrigger.getEntity())
                                                                .append(" ( jobId = ").append(bsTriggerResp.getOutput().getJobId()).append(" )")
                                                                .append(" -> [ ").append(session.state.toUpperCase()).append(" ]")
                                                                .append(System.getProperty("line.separator"));
                                                }


                                                jTextPane.setText(response.toString());


                                            });

                                });
                    });
                });
    }

    /**
     * @param entityName
     * @param mName
     * @param msNumber
     * @param jTextPane
     * @param limit
     * @param row
     * @param info
     * @param enviroment
     */


    public void listingObjectsTest(String entityName, String mName, String msNumber, JTextPane jTextPane, int limit,
                                   int row, String info, String enviroment, boolean delete, JTable tableData) {


        var prop = new PropertyParser();

        try {
            prop.load(ParquetsDialog.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        prop.setProperty("s3.ms.entity", entityName.replace(" ", ""));
        prop.setProperty("s3.ms.name", mName);
        prop.setProperty("s3.ms.number", msNumber);

        WebClient webClient = WebClient.create().mutate().build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("username", "RAP350891002");
        formData.add("password", "Dd3VTAM?z#tbvcpf");

        var pathToken = prop.getProperty("ms.path.token.".concat(enviroment));
        var pathExWrPlain = prop.getProperty("ms.path.writeexecutionplan.".concat(enviroment));
        var basicAuth = prop.getProperty("basicauth.".concat(enviroment));
        var bucketNamePref = prop.getProperty("s3.bucketName.prefix.".concat(enviroment));

        webClient.post().uri(pathToken).header("Authorization", "Basic ".concat(basicAuth))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData)).retrieve().bodyToMono(ResponseToken.class).switchIfEmpty(Mono.error(() -> new Exception("Empty Body to retreive token"))).flatMap(respToken -> {
                    return webClient.post().uri(pathExWrPlain).header("Authorization", "Bearer ".concat(respToken.getAccessToken())).contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("{\n" + "    \"applicationFilter\": \"\",\n" + "    \"dataGovernanceFilter\": \"\",\n" + "    \"source\": \"Self\"\n" + "}")
                            .retrieve().bodyToMono(ResponseWriteExecutionPlan.class).switchIfEmpty(Mono.error(() -> new Exception("Empty Body to retreive WriteExecutionPlan")));

                }).log().subscribe(writeExecutionPlan -> {

                    var response = new StringBuffer();

                    try {


                        var accessKeyID = writeExecutionPlan.getTemporaryCredentials().getWrite().getAccessKeyID();
                        var secretKey = writeExecutionPlan.getTemporaryCredentials().getWrite().getSecretKey();
                        var sessionToken = writeExecutionPlan.getTemporaryCredentials().getWrite().getSessionToken();

                        var bucketName = bucketNamePref.concat(prop.getProperty("s3.bucketName.suffix"));
                        var prefixField = prop.getProperty("s3.prefixField");


                        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(accessKeyID, secretKey, sessionToken);


                        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(sessionCredentials)).withRegion(clientRegion).build();


                        /**
                         *  list of Parquest File
                         */

                        var entity = prop.getProperty("s3.ms.entity");

                        // ListObjectsV2Request objectsV2Request = (new ListObjectsV2Request()).withPrefix("compensatoritalym/v1/").withDelimiter("/").withBucketName("enel-dev-glin-ap31312mp01163-mecompensnetwbtch-cold-area");

                        ListObjectsV2Request objectsV2Request = (new ListObjectsV2Request()).withPrefix(entity + "/v1/").withDelimiter("/").withBucketName(bucketName);

                        ListObjectsV2Result result = s3Client.listObjectsV2(objectsV2Request);

                        if (!result.getCommonPrefixes().isEmpty()) {
                            response.append(info).append(System.getProperty("line.separator"));
                            result.getCommonPrefixes().forEach(s3ObjectPrefix -> {
                                response.append("-----------------------------------------------------------------------------------------------------------------------------------------").append(System.getProperty("line.separator"));
                                response.append("Prefix :: ").append(s3ObjectPrefix).append(System.getProperty("line.separator"));
                                response.append(System.getProperty("line.separator"));
                                var listSum = s3Client.listObjectsV2((new ListObjectsV2Request()).withPrefix(s3ObjectPrefix).withDelimiter("/").withBucketName(bucketName)).getObjectSummaries();
                                response.append("number of files parquet => ").append(String.valueOf(listSum.size())).append(System.getProperty("line.separator"));
                                listSum.subList(0, !delete ? limit : listSum.size()).forEach(s3ObjectSummary -> {
                                    if (delete) {
                                        s3Client.deleteObject(new DeleteObjectRequest(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey()));
                                        response.append("Parquet delete [ ").append(new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(s3ObjectSummary.getLastModified())).append(" ] :: ").append(s3ObjectSummary.getKey()).append(" , size :: ")
                                                .append(FileUtils.byteCountToDisplaySize(s3ObjectSummary.getSize())).append(System.getProperty("line.separator"));
                                    } else {
                                        response.append("Parquet [ ").append(new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(s3ObjectSummary.getLastModified())).append(" ] :: ")
                                                .append(s3ObjectSummary.getKey()).append(" , size :: ").append(FileUtils.byteCountToDisplaySize(s3ObjectSummary.getSize())).append(System.getProperty("line.separator"));

                                        S3Object object = s3Client.getObject(new GetObjectRequest(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey()));
                                        InputStream objectData = object.getObjectContent();
                                        var filename = s3ObjectSummary.getKey().substring(s3ObjectSummary.getKey().lastIndexOf("/") + 1);

                                        if (!filename.equals("_SUCCESS")) {
                                            try {
                                                serviceDownloadS3Files.downloadS3Files(s3Client, s3ObjectSummary, Paths.get("src", "test", "resources", prop.getProperty("s3.prefixField")), filename, row, response, tableData);
                                            } catch (IOException e) {
                                                response.append("Error!  ").append(e.getMessage()).append(System.getProperty("line.separator"));
                                            }
                                        }
                                    }
                                });
                                response.append("-----------------------------------------------------------------------------------------------------------------------------------------").append(System.getProperty("line.separator"));

                            });

                        } else {

                            response.append("-----------------------------------------------------------------------------------------------------------------------------------------").append(System.getProperty("line.separator"));
                            response.append(info).append(" is empty folder => ").append(String.valueOf(result.getObjectSummaries().isEmpty()).toUpperCase()).append(" , number of files parquet => ")
                                    .append(String.valueOf(result.getObjectSummaries().size())).append(System.getProperty("line.separator"));
                            response.append(System.getProperty("line.separator"));

                            if (delete) {
                                result.getObjectSummaries().forEach(s3ObjectSummary -> {
                                    s3Client.deleteObject(new DeleteObjectRequest(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey()));

                                    response.append("Parquet delete [ ").append(new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(s3ObjectSummary.getLastModified())).append(" ] :: ").append(s3ObjectSummary.getKey()).append(" , size :: ")
                                            .append(FileUtils.byteCountToDisplaySize(s3ObjectSummary.getSize())).append(System.getProperty("line.separator"));
                                });
                            } else {

                                result.getObjectSummaries().forEach(s3ObjectSummary -> {
                                    response.append("Parquet [ ").append(new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(s3ObjectSummary.getLastModified())).append(" ] :: ").append(s3ObjectSummary.getKey()).append(" , size :: ")
                                            .append(FileUtils.byteCountToDisplaySize(s3ObjectSummary.getSize())).append(System.getProperty("line.separator"));
                                });
                                response.append("-----------------------------------------------------------------------------------------------------------------------------------------").append(System.getProperty("line.separator"));

                                if (!result.getObjectSummaries().isEmpty() && result.getObjectSummaries().size() >= limit + 1) {

                                    result.getObjectSummaries().subList(0, limit + 1).forEach(objectSummary -> {

                                        S3Object object = s3Client.getObject(new GetObjectRequest(objectSummary.getBucketName(), objectSummary.getKey()));
                                        InputStream objectData = object.getObjectContent();
                                        var filename = objectSummary.getKey().substring(objectSummary.getKey().lastIndexOf("/") + 1);

                                        if (!filename.equals("_SUCCESS")) {
                                            try {
                                                serviceDownloadS3Files.downloadS3Files(s3Client, objectSummary, Paths.get("src", "test", "resources", prop.getProperty("s3.prefixField")), filename, row, response, tableData);
                                            } catch (IOException e) {
                                                response.append("Error!  ").append(e.getMessage()).append(System.getProperty("line.separator"));
                                            }
                                        }

                                    });
                                }
                            }
                        }

                    } catch (Exception e) {
                        response.append("Error!  ").append(e.getMessage()).append(System.getProperty("line.separator"));
                    }

                    System.out.println(response.toString());
                    jTextPane.setText(response.toString());
                });

    }

}
