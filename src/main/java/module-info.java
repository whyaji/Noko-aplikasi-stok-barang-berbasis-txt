module whyaji {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    opens noko to javafx.fxml;
    opens noko.item to javafx.base;
    exports noko;
}
