package com.hasharts.service.fe;

import com.hasharts.db.model.nft.Image;
import com.hasharts.db.model.nft.Nft;
import com.hasharts.db.model.nft.Token;
import com.hasharts.service.hedera.HederaNftService;
import com.hasharts.service.image.GoogleImageGenerator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.ConfigProvider;

@SuppressWarnings("unused")
@JBossLog
@Route("")
public class MainView extends VerticalLayout {

    private final GoogleImageGenerator imageGenerator;
    private final HederaNftService nftService;
    private final String gatewayPrefix;
    private final String gatewayLocalPrefix;

    private final Grid<Token> tokenGrid;
    private Token selectedToken;

    private final Grid<Image> imageGrid;
    private final TextField aiTest;
    private Image selectedImage;

    private final Grid<Nft> nftGrid;

    @Inject
    public MainView(GoogleImageGenerator imageGenerator, HederaNftService nftService) {
        // config
        this.imageGenerator = imageGenerator;
        this.nftService = nftService;
        this.gatewayPrefix = ConfigProvider.getConfig().getValue("nft.token.mint.gateway.prefix", String.class);
        this.gatewayLocalPrefix = ConfigProvider.getConfig()
                .getValue("nft.token.mint.gateway.local.prefix", String.class);
        log.infof("MainView: Image:%s Nft:%s", imageGenerator, nftService);
        // Token
        tokenGrid = tokenGrid();
        // Image
        imageGrid = imageGrid();
        updateImageGrid();
        aiTest = new TextField();
        aiTest.setPlaceholder("Enter text for AI image generation");
        aiTest.setWidth("800px");
        // generate Image
        Button generate = new Button("Generate", e -> {
            try {
                if (aiTest.getValue().trim().isEmpty()) {
                    throw new RuntimeException("Enter text for AI image generation");
                }
                this.imageGenerator.generateAndUpload("nft-image", aiTest.getValue());
                updateImageGrid();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        // mint nft
        Button mint = new Button("Mint", e -> {
            if (selectedToken == null) {
                throw new RuntimeException("Token not selected");
            }
            if (selectedImage == null) {
                throw new RuntimeException("Image not selected");
            }
            try {
                this.nftService.mint(selectedToken, selectedImage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            updateImageGrid();
        });
        // Nft
        nftGrid = nftGrid();
        HorizontalLayout hImages = new HorizontalLayout(aiTest, generate, mint);
        VerticalLayout vImages = new VerticalLayout(hImages, imageGrid);
        // Tabs
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        tabSheet.setWidth("100%");
        tabSheet.setHeight("2200px");
        tabSheet.add("Tokens", new LazyComponent(() -> tokenGrid));
        tabSheet.add("Images", new LazyComponent(() -> vImages));
        tabSheet.add("NFTs", new LazyComponent(() -> nftGrid));
        add(tabSheet);
        addErrorHandling();
    }

    public Grid<Token> tokenGrid() {
        Grid<Token> grid = new Grid<>(Token.class, false);
        grid.addColumn(Token::getId).setHeader("Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Token::getHederaTokenId).setHeader("Hedera Token Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Token::getName).setHeader("Name")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Token::getSymbol).setHeader("Symbol")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addSelectionListener(selection -> {
            Optional<Token> selected = selection.getFirstSelectedItem();
            selected.ifPresent(token -> this.selectedToken = token);
            System.out.println("Selected token: " + selectedToken.getName());
        });
        grid.setAllRowsVisible(true);
        List<Token> tokens = Token.<Token>findAll().list();
        grid.setItems(tokens);
        return grid;
    }

    public Grid<Image> imageGrid() {
        Grid<Image> grid = new Grid<>(Image.class, false);
        grid.addColumn(Image::getId).setHeader("Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Image::getName).setHeader("Name")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(createEmployeeRenderer(gatewayLocalPrefix)).setHeader("Local Url")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(createEmployeeRenderer(gatewayPrefix)).setHeader("Public Url")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Image::getIpfs).setHeader("Ipfs Hash (CID)")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(item -> {
            Button btn = new Button(new Icon(VaadinIcon.TRASH), click -> {
                log.infof("Delete image: %s", item.getId());
                deleteImage(item.getId());
                updateImageGrid();
            });
            btn.addThemeVariants(ButtonVariant.LUMO_ICON);
            return btn;
        });
        grid.setAllRowsVisible(true);
        grid.addSelectionListener(selection -> {
            Optional<Image> selected = selection.getFirstSelectedItem();
            selected.ifPresent(image -> this.selectedImage = image);
            System.out.println("Selected image: " + selectedImage.getName());
        });
        return grid;
    }

    public void updateImageGrid() {
        List<Image> images = Image.<Image>findAll().list();
        imageGrid.setItems(images);
    }

    @Transactional
    public void deleteImage(Long id) {
        Image.deleteById(id);
    }

    public Grid<Nft> nftGrid() {
        Grid<Nft> grid = new Grid<>(Nft.class, false);
        grid.addColumn(Nft::getHederaTokenId).setHeader("Hedera Token Id")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Nft::getSerials).setHeader("Serials")
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Nft::getData).setHeader("Data")
                .setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        return grid;
    }

    private static Renderer<Image> createEmployeeRenderer(String prefix) {
        return LitRenderer.<Image>of(
//                        "<vaadin-avatar img=\"${item.url}\" name=\"${item.name}\" alt=\"Image\"></vaadin-avatar>")
                        "<img src=\"${item.url}\" name=\"${item.name}\" width=\"100\" alt=\"Image\"></img>")
                .withProperty("url", e -> prefix + e.getIpfs())
                .withProperty("name", Image::getName);
    }

    public static class LazyComponent extends Div {
        public LazyComponent(SerializableSupplier<? extends Component> supplier) {
            addAttachListener(e -> {
                if (getElement().getChildCount() == 0) {
                    add(supplier.get());
                }
            });
        }
    }

    private void addErrorHandling() {
        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());
    }
}
