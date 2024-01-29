package com.example.song_finder_fx.Model;

public class FUGAReport {
    private String saleStartDate;
    private String saleEndDate;
    private String dsp;
    private String saleStoreName;
    private String saleType;
    private String saleUserType;
    private String territory;
    private long productUPC = 0;
    private long productReference = 0;
    private String productCatalogNumber;
    private String productLabel;
    private String productArtist;
    private String productTitle;
    private String assetArtist;
    private String assetTitle;
    private String assetVersion;
    private int assetDuration = 0;
    private String assetISRC;
    private long assetReference = 0;
    private String assetOrProduct;
    private int productQuantity = 0;
    private int assetQuantity = 0;
    private double originalGrossIncome = 0;
    private String originalCurrency;
    private double exchangeRate = 0;
    private double convertedGrossIncome = 0;
    private String contractDealTerm;
    private double reportedRoyalty = 0;
    private String currency;
    private int reportRunID = 0;
    private int reportID = 0;
    private long saleID = 0;

    // Getters
    public String getSaleStartDate() {
        return saleStartDate;
    }

    public String getSaleEndDate() {
        return saleEndDate;
    }

    public String getDsp() {
        return dsp;
    }

    public String getSaleStoreName() {
        return saleStoreName;
    }

    public String getSaleType() {
        return saleType;
    }

    public String getSaleUserType() {
        return saleUserType;
    }

    public String getTerritory() {
        return territory;
    }

    public long getProductUPC() {
        return productUPC;
    }

    public long getProductReference() {
        return productReference;
    }

    public String getProductCatalogNumber() {
        return productCatalogNumber;
    }

    public String getProductLabel() {
        return productLabel;
    }

    public String getProductArtist() {
        return productArtist;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getAssetArtist() {
        return assetArtist;
    }

    public String getAssetTitle() {
        return assetTitle;
    }

    public String getAssetVersion() {
        return assetVersion;
    }

    public int getAssetDuration() {
        return assetDuration;
    }

    public String getAssetISRC() {
        return assetISRC;
    }

    public long getAssetReference() {
        return assetReference;
    }

    public String getAssetOrProduct() {
        return assetOrProduct;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public int getAssetQuantity() {
        return assetQuantity;
    }

    public double getOriginalGrossIncome() {
        return originalGrossIncome;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public double getConvertedGrossIncome() {
        return convertedGrossIncome;
    }

    public String getContractDealTerm() {
        return contractDealTerm;
    }

    public double getReportedRoyalty() {
        return reportedRoyalty;
    }

    public String getCurrency() {
        return currency;
    }

    public int getReportRunID() {
        return reportRunID;
    }

    public int getReportID() {
        return reportID;
    }

    public long getSaleID() {
        return saleID;
    }


    // Setters
    public void setSaleStartDate(String saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public void setSaleEndDate(String saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public void setDsp(String dsp) {
        this.dsp = dsp;
    }

    public void setSaleStoreName(String saleStoreName) {
        this.saleStoreName = saleStoreName;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setSaleUserType(String saleUserType) {
        this.saleUserType = saleUserType;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public void setProductUPC(String productUPC) {
        try {
            this.productUPC = Long.parseLong(productUPC);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setProductReference(String productReference) {
        try {
            this.productReference = Long.parseLong(productReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setProductCatalogNumber(String productCatalogNumber) {
        this.productCatalogNumber = productCatalogNumber;
    }

    public void setProductLabel(String productLabel) {
        this.productLabel = productLabel;
    }

    public void setProductArtist(String productArtist) {
        this.productArtist = productArtist;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setAssetArtist(String assetArtist) {
        this.assetArtist = assetArtist;
    }

    public void setAssetTitle(String assetTitle) {
        this.assetTitle = assetTitle;
    }

    public void setAssetVersion(String assetVersion) {
        this.assetVersion = assetVersion;
    }

    public void setAssetDuration(String assetDuration) {
        try {
            this.assetDuration = Integer.parseInt(assetDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAssetISRC(String assetISRC) {
        this.assetISRC = assetISRC;
    }

    public void setAssetReference(String assetReference) {
        try {
            this.assetReference = Long.parseLong(assetReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAssetOrProduct(String assetOrProduct) {
        this.assetOrProduct = assetOrProduct;
    }

    public void setProductQuantity(String productQuantity) {
        try {
            this.productQuantity = Integer.parseInt(productQuantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAssetQuantity(String assetQuantity) {
        try {
            this.assetQuantity = Integer.parseInt(assetQuantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOriginalGrossIncome(String originalGrossIncome) {
        try {
            this.originalGrossIncome = Double.parseDouble(originalGrossIncome);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }

    public void setExchangeRate(String exchangeRate) {
        try {
            this.exchangeRate = Double.parseDouble(exchangeRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConvertedGrossIncome(String convertedGrossIncome) {
        try {
            this.convertedGrossIncome = Double.parseDouble(convertedGrossIncome);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setContractDealTerm(String contractDealTerm) {
        this.contractDealTerm = contractDealTerm;
    }

    public void setReportedRoyalty(String reportedRoyalty) {
        try {
            this.reportedRoyalty = Double.parseDouble(reportedRoyalty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setReportRunID(String reportRunID) {
        try {
            this.reportRunID = Integer.parseInt(reportRunID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReportID(String reportID) {
        try {
            this.reportID = Integer.parseInt(reportID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSaleID(String saleID) {
        try {
            this.saleID = Long.parseLong(saleID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
